package com.simplevat.entity;

import com.simplevat.entity.converter.DateConverter;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

/**
 * Created by mohsinh on 2/26/2017.
 */
@NamedQueries({
    @NamedQuery(name = "allContacts",
            query = "SELECT c "
            + "FROM Contact c where c.deleteFlag = FALSE order by c.firstName, c.lastName")
    ,
    
    @NamedQuery(name = "Contact.contactByEmail",
            query = "SELECT c "
            + "FROM Contact c where c.email =:email")
    ,
    @NamedQuery(name = "Contact.contactsByName",
            query = "SELECT c FROM Contact c WHERE  ((c.firstName LIKE :name or c.lastName LIKE :name) and c.deleteFlag = FALSE and c.contactType=:contactType) order by c.firstName, c.lastName")
})

@Entity
@Table(name = "CONTACT")
@Data
@TableGenerator(name="INCREMENT_INITIAL_VALUE", initialValue = 1000)
public class Contact implements Serializable {

    private static final long serialVersionUID = 6914121175305098995L;

    @Id
    @Column(name = "CONTACT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY,generator ="INCREMENT_INITIAL_VALUE")
    private Integer contactId;
    @Basic
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Basic
    @Column(name = "MIDDLE_NAME")
    private String middleName;
    @Basic
    @Column(name = "LAST_NAME")
    private String lastName;
    @Basic
    @Column(name = "ORGANIZATION")
    private String organization;
    @Basic
    @Column(name = "EMAIL")
    private String email;
    @Basic
    @Column(name = "BILLING_EMAIL")
    private String billingEmail;
    @Basic
    @Column(name = "TELEPHONE")
    private String telephone;
    @Basic
    @Column(name = "MOBILE_NUMBER")
    private String mobileNumber;
    @Basic
    @Column(name = "INVOICING_ADDRESS_LINE1")
    private String invoicingAddressLine1;
    @Basic
    @Column(name = "INVOICING_ADDRESS_LINE2")
    private String invoicingAddressLine2;
    @Basic
    @Column(name = "INVOICING_ADDRESS_LINE3")
    private String invoicingAddressLine3;
    @Basic
    @Column(name = "CITY")
    private String city;
    @Basic
    @Column(name = "STATE_REGION")
    private String stateRegion;
    @Basic
    @Column(name = "POST_ZIP_CODE")
    private String postZipCode;
    @Basic
    @Column(name = "PO_BOX_NUMBER")
    private String poBoxNumber;

    @Basic
    @Column(name = "CONTRACT_PO_NUMBER")
    private String contractPoNumber;
    @Basic
    @Column(name = "VAT_REGISTRATION_NUMBER")
    private String vatRegistrationNumber;

    @Basic(optional = false)
    @Column(name = "CREATED_BY")
    private Integer createdBy;

    @Basic(optional = false)
    @Column(name = "CREATED_DATE")
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    @Convert(converter = DateConverter.class)
    private LocalDateTime createdDate;

    @Basic
    @Column(name = "LAST_UPDATED_BY")
    private Integer lastUpdatedBy;

    @Basic
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_CODE")
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_LANGUAGE_CODE")
    private Language language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENCY_CODE")
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TITLE_CODE")
    private Title title;
    
    @Basic
    @Column(name = "CONTACT_TYPE")
    private Integer contactType;

    @PrePersist
    public void updateDates() {
        createdDate = LocalDateTime.now();
        lastUpdateDate = LocalDateTime.now();
    }

    @PreUpdate
    public void updateLastUpdatedDate() {
        lastUpdateDate = LocalDateTime.now();
    }

}
