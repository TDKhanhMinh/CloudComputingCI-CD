package com.example.Final.service.Payment;

import com.example.Final.entity.paymentservice.UserPayment;
import com.example.Final.entity.paymentservice.UserPaymentDetails;
import com.example.Final.entity.securityservice.User;
import com.example.Final.repository.UserPaymentDetailsRepo;
import com.example.Final.repository.UserPaymentRepo;
import com.example.Final.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class VNPayPayment implements PaymentProvider{
    @Autowired
    private UserPaymentDetailsRepo userPaymentDetailsRepo;
    @Autowired
    private UserPaymentRepo userPaymentRepo;
    @Autowired
    private UserService userService;

    public void createUserPayment(User user, double amount) {

        UserPayment userPayment = user.getUserPayment();
        List<UserPaymentDetails> detailsList = userPayment.getPaymentDetails();

        double balance = amount - amount * 0.1;
        UserPaymentDetails paymentDetails = new UserPaymentDetails();

        paymentDetails.setPaymentMethod("vnpay");
        paymentDetails.setAmount(balance);
        paymentDetails.setStatus("Đã thanh toán");
        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDate.format(formatter);
        paymentDetails.setPayment(userPayment);
        paymentDetails.setDate(formattedDate);
        userPaymentDetailsRepo.save(paymentDetails);

        detailsList.add(paymentDetails);
        userPayment.setPaymentDetails(detailsList);

        userPaymentRepo.save(userPayment);
        user.setAccountBalance(user.getAccountBalance() + balance);
        userService.save(user);
    }
}
