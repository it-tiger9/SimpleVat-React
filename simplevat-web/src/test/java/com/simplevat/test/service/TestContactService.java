package com.simplevat.test.service;

import com.simplevat.entity.Contact;
import com.simplevat.entity.Country;
import com.simplevat.test.common.BaseManagerTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

/**
 * Created by mohsin on 3/4/2017.
 */

public class TestContactService extends BaseManagerTest {

    @Test
    @Transactional
    public void testGetContacts()
    {
        List<Contact> contacts = this.contactService.getContacts(new Integer(0), new Integer(20));

        System.out.println(" Size of contacts :" + contacts.size());

        Iterator<Contact> contactIterator = contacts.iterator();
        while(contactIterator.hasNext())
        {
            Contact contact = contactIterator.next();
            System.out.println(contact.getContactId()+" - "+contact.getFirstName()+" "+contact.getLastName()+" Country :"+contact.getCountry().getCountryName());
        }
    }

    @Test
    public void testCreateContact()
    {
        Contact contact = new Contact();
        contact.setFirstName("Mohsin");
        contact.setLastName("Hashmi");
        Country country = new Country();
        country.setCountryCode(10);
        contact.setCountry(country);

        contactService.createContact(contact);

        System.out.println(contact.getContactId()+" - "+contact.getFirstName()+" "+contact.getLastName());
    }

    @Test
    public void testUpdateContact() throws Exception {
        /* Insert new Contact to check edit */
        Contact contact = new Contact();
        contact.setFirstName("First Name");
        contact.setLastName("Last Name");
        Country country = new Country();
        country.setCountryCode(10);
        contact.setCountry(country);

        /*Insert*/
        final Contact insertedContact = contactService.createContact(contact);
        Assert.assertNotNull(insertedContact);

        contact.setContactId(insertedContact.getContactId());
        final String updatedFirstName = "UPDATE NAME";
        contact.setFirstName(updatedFirstName);

        /*Update*/
        Contact updatedContact = contactService.updateContact(contact);

        /*Assert*/
        Assert.assertNotNull(updatedContact);
        Assert.assertEquals(updatedContact.getFirstName(), updatedFirstName);

    }
}
