package com.simplevat.web.invoice.controller;

import com.simplevat.criteria.ProjectCriteria;
import com.simplevat.entity.Contact;
import com.simplevat.entity.Currency;
import com.simplevat.entity.Project;
import com.simplevat.entity.invoice.DiscountType;
import com.simplevat.entity.invoice.Invoice;
import com.simplevat.service.ContactService;
import com.simplevat.service.CurrencyService;
import com.simplevat.service.DiscountTypeService;
import com.simplevat.service.ProjectService;
import com.simplevat.service.invoice.InvoiceService;
import com.simplevat.web.contact.model.ContactModel;
import com.simplevat.web.invoice.model.InvoiceItemModel;
import com.simplevat.web.invoice.model.InvoiceModel;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.mysql.jdbc.StringUtils.isNullOrEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.annotation.SessionScope;

/**
 * @author Hiren
 */
@Controller
@SessionScope
public class InvoiceController implements Serializable {

    private static final long serialVersionUID = 6299117288316809011L;

    @Getter
    private InvoiceModel invoiceModel;

    @Getter
    @Setter
    private ContactModel contactModel;

    @Getter
    @Setter
    private List<Currency> currencies;

    @Autowired
    private ContactService contactService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private InvoiceModelConverter invoiceConverter;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private DiscountTypeService discountTypeService;

    @Autowired
    private ProjectService projectService;

    @PostConstruct
    public void initInvoice() {
        invoiceModel = new InvoiceModel();
        contactModel = new ContactModel();
        currencies = currencyService.getCurrencies();
        setDefaultCurrency();
        invoiceModel.setInvoiceDate(new Date());
        invoiceModel.setInvoiceDueOn(30);
        updateCurrencyLabel();
        invoiceModel.addInvoiceItem(new InvoiceItemModel());
    }

    private void setDefaultCurrency() {
        Currency defaultCurrency = currencyService.getDefaultCurrency();
        if (defaultCurrency != null) {
            invoiceModel.setCurrencyCode(defaultCurrency);
        }
    }

    public void addInvoiceItem() {
        boolean validated = validateInvoiceLineItems();
        if (validated) {
            updateCurrencyLabel();
            invoiceModel.addInvoiceItem(new InvoiceItemModel());
        }

    }

    private boolean validateInvoiceLineItems() {
        boolean validated = true;
        for (InvoiceItemModel lastItem : invoiceModel.getInvoiceItems()) {
            if (lastItem.getQuatity() < 1 || lastItem.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                FacesMessage message = new FacesMessage("Please enter proper detail for all invoice items.");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                FacesContext.getCurrentInstance().addMessage("validationId", message);
                validated = false;
            }
        }
        return validated;
    }

    private boolean validateAtLeastOneItem() {
        if (invoiceModel.getInvoiceItems().size() < 1) {
            FacesMessage message = new FacesMessage("Please add atleast one item to create Invoice.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage("validationId", message);
            return false;
        }
        return true;
    }

    public List<Contact> contacts(final String searchQuery) {
        return contactService.getContacts(searchQuery.trim());
    }

    public List<Project> projects(final String searchQuery) throws Exception {
        ProjectCriteria criteria = new ProjectCriteria();
        criteria.setActive(Boolean.TRUE);
        if (!isNullOrEmpty(searchQuery)) {
            criteria.setProjectName(searchQuery);
        }
        return projectService.getProjectsByCriteria(criteria);
    }

    public List<Currency> completeCurrency(String currencyStr) {
        List<Currency> currencySuggestion = new ArrayList<>();
        Iterator<Currency> currencyIterator = this.currencies.iterator();

        while (currencyIterator.hasNext()) {
            Currency currency = currencyIterator.next();
            if (currency.getCurrencyName() != null
                    && !currency.getCurrencyName().isEmpty()
                    && currency.getCurrencyName().toUpperCase().contains(currencyStr.toUpperCase())) {
                currencySuggestion.add(currency);
            } else if (currency.getCurrencyDescription() != null
                    && !currency.getCurrencyDescription().isEmpty()
                    && currency.getCurrencyDescription().toUpperCase().contains(currencyStr.toUpperCase())) {
                currencySuggestion.add(currency);
            } else if (currency.getCurrencyIsoCode() != null
                    && !currency.getCurrencyIsoCode().isEmpty()
                    && currency.getCurrencyIsoCode().toUpperCase().contains(currencyStr.toUpperCase())) {
                currencySuggestion.add(currency);
            }
        }
        return currencySuggestion;
    }

    public List<DiscountType> discountTypes(final String searchString) {
        final List<DiscountType> types = discountTypeService.getDiscountTypes();
        for (DiscountType type : types) {
            if (null != searchString
                    && !type.toString().toLowerCase().contains(searchString.toLowerCase())) {
                types.remove(type);
            }
        }
        return types;
    }

    public void saveInvoice() throws IOException {
        if (!validateInvoiceLineItems() || !validateAtLeastOneItem()) {
            return;
        }
        final Invoice invoice = invoiceConverter
                .convertModelToEntity(invoiceModel);
        invoiceService.persist(invoice, 0);
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (invoiceModel.getInvoiceId() > 0) {
            context.addMessage(null, new FacesMessage("Invoice Updated SuccessFully"));
        } else {
            context.addMessage(null, new FacesMessage("Invoice Added SuccessFully"));
        }
        initInvoice();
        context.getExternalContext()
                .redirect("list.xhtml?faces-redirect=true");
    }

    public void saveAndAddMoreInvoice() {
        if (!validateInvoiceLineItems() || !validateAtLeastOneItem()) {
            return;
        }
        final Invoice invoice = invoiceConverter
                .convertModelToEntity(invoiceModel);
        invoiceService.update(invoice, invoice.getInvoiceId());
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (invoiceModel.getInvoiceId() > 0) {
            context.addMessage(null, new FacesMessage("Invoice Updated SuccessFully"));
        } else {
            context.addMessage(null, new FacesMessage("Invoice Added SuccessFully"));
        }
        initInvoice();

    }

    public void updateCurrency() {
        setDefaultCurrency();
        if (null != invoiceModel.getContact()) {
            final Contact contact = contactService
                    .getContact(invoiceModel.getContact().getContactId());
            invoiceModel.setCurrencyCode(contact.getCurrency());
        }
    }

    public void updateCurrencyLabel() {
        if (null != invoiceModel.getCurrencyCode()) {
            invoiceModel.setCurrencyCode(currencyService.getCurrency(invoiceModel.getCurrencyCode().getCurrencyCode()));
        }
    }

    public void updateSubTotal(final InvoiceItemModel invoiceItemModel) {
        final int quantity = invoiceItemModel.getQuatity();
        final BigDecimal unitPrice = invoiceItemModel.getUnitPrice();
        final BigDecimal vatPer = invoiceItemModel.getVatId();
        if (null != unitPrice) {
            final BigDecimal amountWithoutTax = unitPrice.multiply(new BigDecimal(quantity));
            invoiceItemModel.setSubTotal(amountWithoutTax);

            if (vatPer != null && vatPer.compareTo(BigDecimal.ZERO) >= 1) {
                final BigDecimal amountWithTax = amountWithoutTax
                        .add(amountWithoutTax.multiply(vatPer).multiply(new BigDecimal(0.01)));
                invoiceItemModel.setSubTotal(amountWithTax);
            }
        }

    }


    public void initCreateContact() {
        contactModel = new ContactModel();
    }

    public void createContact() {
        Currency defaultCurrency = currencyService.getDefaultCurrency();

        final Contact contact = new Contact();

        contact.setBillingEmail(contactModel.getEmailAddress());
        contact.setDeleteFlag(Boolean.FALSE);
        contact.setEmail(contactModel.getEmailAddress());
        contact.setFirstName(contactModel.getFirstName());
        contact.setLastName(contactModel.getLastName());
        contact.setOrganization(contactModel.getOrganizationName());
        contact.setCreatedBy(1);
        contact.setCurrency(defaultCurrency);

        contactModel = new ContactModel();

        if (defaultCurrency != null) {
            contactModel.setCurrency(defaultCurrency);
        }

        contactService.createOrUpdateContact(contact);

        invoiceModel.setContact(contact);

    }

}