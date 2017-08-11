package com.simplevat.web.invoice.controller;

import com.github.javaplugs.jsf.SpringScopeView;
import com.simplevat.entity.invoice.Invoice;
import com.simplevat.entity.invoice.InvoiceLineItem;
import com.simplevat.service.invoice.InvoiceService;
import com.simplevat.web.invoice.model.InvoiceItemModel;
import com.simplevat.web.invoice.model.InvoiceModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Hiren
 *
 */
@Controller
@SpringScopeView
public class InvoiceModelHelper {

    @Autowired
    private InvoiceService invoiceService;

    public Invoice getInvoiceEntity(InvoiceModel invoiceModel) {
        final LocalDateTime invoiceDate = LocalDateTime.ofInstant(invoiceModel.getInvoiceDate().toInstant(), ZoneId.systemDefault());

        Invoice invoice;

        if (invoiceModel.getInvoiceId() > 0) {
            invoice = invoiceService.findByPK(invoiceModel.getInvoiceId());
        } else {
            invoice = new Invoice();
        }

        invoice.setContractPoNumber(invoiceModel.getContractPoNumber());
        invoice.setCurrency(invoiceModel.getCurrencyCode());
        invoice.setInvoiceProject(invoiceModel.getProject());
        invoice.setInvoiceContact(invoiceModel.getContact());
        invoice.setInvoiceDate(invoiceDate);
        invoice.setInvoiceDiscount(invoiceModel.getDiscount());
        invoice.setDiscountType(invoiceModel.getDiscountType());
        invoice.setInvoiceDueOn(invoiceModel.getInvoiceDueOn());
        invoice.setInvoiceReferenceNumber(invoiceModel.getInvoiceRefNo());
        invoice.setInvoiceText(invoiceModel.getInvoiceText());

        final Collection<InvoiceLineItem> items = invoiceModel
                .getInvoiceItems()
                .stream()
                .map((item) -> convertToLineItem(item, invoice))
                .collect(Collectors.toList());

        invoice.setInvoiceLineItems(items);
        invoice.setCreatedBy(1);

        return invoice;
    }

    @Nonnull
    private InvoiceLineItem convertToLineItem(@Nonnull final InvoiceItemModel model,
            @Nonnull final Invoice invoice) {
        final InvoiceLineItem item = new InvoiceLineItem();
        if (model.getId() > 0) {
            item.setInvoiceLineItemId(model.getId());
        }
        item.setCreatedDate(Calendar.getInstance());

        item.setInvoiceLineItemDescription(model.getDescription());
        item.setInvoiceLineItemQuantity(model.getQuatity());
        item.setInvoiceLineItemUnitPrice(model.getUnitPrice());
        item.setInvoiceLineItemVat(model.getVatId());
        item.setCreatedBy(1);
        item.setLastUpdateDate(Calendar.getInstance());
        item.setInvoice(invoice);

        return item;
    }

    
    public InvoiceModel getInvoiceModel(Invoice invoice) {

        InvoiceModel invoiceModel = new InvoiceModel();

        invoiceModel.setContractPoNumber(invoice.getContractPoNumber());
        invoiceModel.setCurrencyCode(invoice.getCurrency());
        invoiceModel.setInvoiceId(invoice.getInvoiceId());
        invoiceModel.setContact(invoice.getInvoiceContact());
        invoiceModel.setProject(invoice.getInvoiceProject());

        invoiceModel.setInvoiceDate(null != invoice.getInvoiceDate() ? Date.from(invoice.getInvoiceDate().atZone(ZoneId.systemDefault()).toInstant()) : null);
        invoiceModel.setDiscount(invoice.getInvoiceDiscount());
        invoiceModel.setDiscountType(invoice.getDiscountType());
        invoiceModel.setInvoiceDueOn(invoice.getInvoiceDueOn());
        invoiceModel.setInvoiceRefNo(invoice.getInvoiceReferenceNumber());
        invoiceModel.setInvoiceText(invoice.getInvoiceText());

        final List<InvoiceItemModel> items = invoice
                .getInvoiceLineItems()
                .stream()
                .map((lineItem) -> convertToItemModel(lineItem))
                .collect(Collectors.toList());

        invoiceModel.setInvoiceItems(items);

        return invoiceModel;
    }

    @Nonnull
    public InvoiceItemModel convertToItemModel(@Nonnull final InvoiceLineItem invoiceLineItem) {

        final InvoiceItemModel model = new InvoiceItemModel();

        model.setId(invoiceLineItem.getInvoiceLineItemId());
        model.setDescription(invoiceLineItem.getInvoiceLineItemDescription());
        model.setQuatity(invoiceLineItem.getInvoiceLineItemQuantity());
        model.setUnitPrice(invoiceLineItem.getInvoiceLineItemUnitPrice());
        model.setVatId(invoiceLineItem.getInvoiceLineItemVat());

        this.updateSubTotal(model);

        return model;

    }

    private void updateSubTotal(@Nonnull final InvoiceItemModel invoiceItemModel) {
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
}