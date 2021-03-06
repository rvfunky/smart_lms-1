package com.lms.cmpe.service;

import com.lms.cmpe.dao.TransactionDao;
import com.lms.cmpe.model.Book;
import com.lms.cmpe.model.Transaction;
import com.lms.cmpe.model.TransactionBooks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Nischith on 11/29/2016.
 */
@Service
public class TransactionServiceImpl implements TransactionService{

    @Autowired
    private TransactionDao transactionDao;

    @Override
    public Transaction checkOutBooks(Transaction transaction, int userId, Date appTime) { return transactionDao.checkOutBooks(transaction, userId,appTime); }

    @Override
    public boolean returnBooks(ArrayList<TransactionBooks> transactionBooksList, int userId, Date appTime) { return transactionDao.returnBooks(transactionBooksList, userId, appTime); }

    @Override
    public List<Book> getCheckedOutBooksByUser(int userId) { return transactionDao.getCheckedOutBooksByUser(userId); }

    @Override
    public List<TransactionBooks> getBooksToBeReturned(int userId) {
        return transactionDao.getBooksToBeReturned(userId);
    }

    @Override
    public int reissueBook(int transactionBookId, int userId) {
        return transactionDao.reissueBook(transactionBookId, userId);
    }
}
