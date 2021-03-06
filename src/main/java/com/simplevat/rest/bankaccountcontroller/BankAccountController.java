/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simplevat.rest.bankaccountcontroller;

import com.simplevat.bank.model.DeleteModel;
import com.simplevat.helper.BankHelper;
import com.simplevat.security.JwtTokenUtil;
import com.simplevat.contact.model.BankModel;
import com.simplevat.entity.Country;
import com.simplevat.entity.Currency;
import com.simplevat.entity.User;
import com.simplevat.entity.bankaccount.BankAccount;
import com.simplevat.entity.bankaccount.BankAccountStatus;
import com.simplevat.entity.bankaccount.BankAccountType;
import com.simplevat.service.BankAccountTypeService;
import com.simplevat.service.CountryService;
import com.simplevat.service.CurrencyService;
import com.simplevat.service.UserServiceNew;
import com.simplevat.service.BankAccountService;
import com.simplevat.service.BankAccountStatusService;
import com.simplevat.service.bankaccount.TransactionStatusService;

import io.swagger.annotations.ApiOperation;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.type.LocalDateTimeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Sonu
 */
@RestController
@RequestMapping(value = "/rest/bank")
public class BankAccountController implements Serializable {

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private BankAccountStatusService bankAccountStatusService;

    @Autowired
    private UserServiceNew userServiceNew;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private BankAccountTypeService bankAccountTypeService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private TransactionStatusService transactionStatusService;
    
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @ApiOperation(value = "Get All Bank Accounts", response = List.class)
    @GetMapping(value = "/getbanklist")
    public ResponseEntity getBankAccountList() {
        List<BankAccount> bankAccounts = bankAccountService.getBankAccounts();
        if (bankAccounts != null) {
            return new ResponseEntity<>(bankAccounts, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Add New Bank Account", response = BankAccount.class)
    @PostMapping
    public ResponseEntity saveBankAccount(@RequestBody BankModel bankModel, HttpServletRequest request) {
        try {
        	Integer userId = jwtTokenUtil.getUserIdFromHttpRequest(request);
            BankAccount bankAccount = BankHelper.createBankAccountByBankAccountModel(bankModel, bankAccountService, bankAccountStatusService, currencyService, bankAccountTypeService, countryService);
            User user = userServiceNew.findByPK(userId);
            if (bankModel.getBankAccountId() == null) {
                if (user != null) {
                    bankAccount.setCreatedDate(LocalDateTime.now());
                    bankAccount.setCreatedBy(user.getUserId());
                }
                bankAccountService.persist(bankAccount);
                return new ResponseEntity<>(bankAccount,HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ApiOperation(value = "Update Bank Account", response = BankAccount.class)
    @PutMapping("/{bankAccountId}")
    public ResponseEntity updateBankAccount(@PathVariable("bankAccountId") Integer bankAccountId, BankModel bankModel, HttpServletRequest request) {
        try {
        	Integer userId = jwtTokenUtil.getUserIdFromHttpRequest(request);
        	bankModel.setBankAccountId(bankAccountId);
            BankAccount bankAccount = BankHelper.getBankAccountByBankAccountModel(bankModel, bankAccountService, bankAccountStatusService, currencyService, bankAccountTypeService, countryService);
            User user = userServiceNew.findByPK(userId);
                bankAccount.setBankAccountId(bankModel.getBankAccountId());
                bankAccount.setLastUpdateDate(LocalDateTime.now());
                bankAccount.setLastUpdatedBy(user.getUserId());
                bankAccountService.update(bankAccount);
                return new ResponseEntity<>(HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ApiOperation(value = "Get All Bank Account Types")
    @GetMapping(value = "/getaccounttype")
    public ResponseEntity getBankAccontType() {
        List<BankAccountType> bankAccountTypes = bankAccountTypeService.getBankAccountTypeList();
        if (bankAccountTypes != null && !bankAccountTypes.isEmpty()) {
            return new ResponseEntity<>(bankAccountTypes, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Get All Bank Account Status")
    @GetMapping(value = "/getbankaccountstatus")
    public ResponseEntity getBankAccountStatus() {
        List<BankAccountStatus> bankAccountStatuses = bankAccountStatusService.getBankAccountStatuses();
        if (bankAccountStatuses != null && !bankAccountStatuses.isEmpty()) {
            return new ResponseEntity<>(bankAccountStatuses, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Deprecated
    @GetMapping(value = "/getcountry")
    public ResponseEntity getCountry() {
        try {
            List<Country> countries = countryService.getCountries();
            if (countries != null && !countries.isEmpty()) {
                return new ResponseEntity<>(countries, HttpStatus.OK);
            } else {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ApiOperation(value = "Delete the Bank Account", response = BankAccount.class)
    @DeleteMapping(value = "/{bankAccountId}")
    public ResponseEntity deleteBankAccount(@PathVariable("bankAccountId") Integer bank_account_id, HttpServletRequest request) {
    	try {
    	Integer userId = jwtTokenUtil.getUserIdFromHttpRequest(request);
    	
    	BankAccount bankAccount = bankAccountService.findByPK(bank_account_id);
        if(bankAccount!=null) {
        	bankAccount.setLastUpdateDate(LocalDateTime.now());
            bankAccount.setLastUpdatedBy(userId);
            bankAccount.setDeleteFlag(true);
            bankAccountService.update(bankAccount);
            return new ResponseEntity<>(bankAccount,HttpStatus.OK);
        }else {
        	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ApiOperation(value = "Get Bank Account by Bank Account ID", response = BankAccount.class)
    @GetMapping(value = "/getbyid")
    public ResponseEntity getById(@RequestParam("id") Integer id) {
        try {
            BankAccount bankAccount = bankAccountService.findByPK(id);
            return new ResponseEntity<>(bankAccount, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ApiOperation(value = "Delete Bank Accounts")
    @DeleteMapping(value = "/multiple")
    public ResponseEntity deleteBankAccounts(@RequestBody DeleteModel ids, HttpServletRequest httpServletRequest) {
        try {
        	bankAccountService.deleteByIds(ids.getIds());
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Deprecated
    @GetMapping(value = "/getcurrenncy")
    public ResponseEntity getCurrency() {
        try {
            List<Currency> currencies = currencyService.getCurrencies();
            if (currencies != null && !currencies.isEmpty()) {
                return new ResponseEntity<>(currencies, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
