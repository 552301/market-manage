package cn.lmjia.market.core.entity.withdraw;

import cn.lmjia.market.core.define.Money;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.WithdrawStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现申请
 */
@Entity
@Setter
@Getter
public class WithdrawRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 发起人
     */
    @ManyToOne
    private Login whose;

    /**
     * 收款人姓名
     */
    @Column(length = 20)
    private String payee;

    /**
     * 收款账号
     */
    @Column(length = 20)
    private String account;

    /**
     * 开户行
     */
    @Column(length = 20)
    private String bank;

    /**
     * 收款人电话
     */
    @Column(length = 20)
    private String mobile;

    /**
     * 提现金额
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal amount;
    /**
     * 转账金额
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal actualAmount;

    /**
     * 提现申请时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime requestTime;

    /**
     * 提现的状态
     */
    private WithdrawStatus withdrawStatus;

    /**
     * 是否提供发票
     */
    private boolean invoice;

    @Column(length = 20)
    private String logisticsCode;
    @Column(length = 20)
    private String logisticsCompany;

    /**
     * 转账单据编号
     */
    @Column(length = 50)
    private String transactionRecordNumber;

    /**
     * 备注信息，最多100个字
     */
    @Column(length = 100)
    private String comment;
    /**
     * 处理时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime manageTime;
    /**
     * 处理人
     */
    @ManyToOne
    private Manager manageBy;

    public Money getActualAmountMoney() {
        return new Money(actualAmount);
    }

    public Money getAmountMoney() {
        return new Money(amount);
    }

    public Money getTaxFee() {
        return new Money(amount.subtract(actualAmount));
    }
}
