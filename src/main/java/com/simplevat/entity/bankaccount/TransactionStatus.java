package com.simplevat.entity.bankaccount;

import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Data;

import com.simplevat.entity.converter.DateConverter;
import java.io.Serializable;
import org.hibernate.annotations.ColumnDefault;

@NamedQueries({
    @NamedQuery(name = "findAllTransactionStatues",
            query = "SELECT t FROM TransactionStatus t where t.deleteFlag = FALSE order by t.defaltFlag DESC, t.orderSequence,t.explainationStatusName ASC")
})
@Entity
@Table(name = "EXPLANATION_STATUS")
@Data
public class TransactionStatus implements Serializable {

    private static final long serialVersionUID = 848122185643690684L;
    @Id
    @Column(name = "EXPLANATION_STATUS_CODE")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int explainationStatusCode;

    @Basic(optional = false)
    @Column(name = "EXPLANATION_STATUS_NAME")
    private String explainationStatusName;

    @Basic
    @Column(name = "EXPLANATION_STATUS_DESCRIPTION")
    private String explainationStatusDescriptions;

    @Column(name = "DEFAULT_FLAG")
    @ColumnDefault(value = "'N'")
    @Basic(optional = false)
    private Character defaltFlag;

    @Column(name = "ORDER_SEQUENCE")
    @Basic(optional = true)
    private Integer orderSequence;

    @Column(name = "CREATED_BY")
    @Basic(optional = false)
    private Integer createdBy;

    @Column(name = "CREATED_DATE")
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    @Basic(optional = false)
    @Convert(converter = DateConverter.class)
    private LocalDateTime createdDate;

    @Column(name = "LAST_UPDATED_BY")
    private Integer lastUpdateBy;

    @Column(name = "LAST_UPDATE_DATE")
    @Convert(converter = DateConverter.class)
    private LocalDateTime lastUpdateDate;

    @Column(name = "DELETE_FLAG")
    @ColumnDefault(value = "0")
    @Basic(optional = false)
    private Boolean deleteFlag = Boolean.FALSE;

    @Column(name = "VERSION_NUMBER")
    @ColumnDefault(value = "1")
    @Basic(optional = false)
    @Version
    private Integer versionNumber;

}
