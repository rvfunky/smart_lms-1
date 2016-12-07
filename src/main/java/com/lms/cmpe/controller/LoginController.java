package com.lms.cmpe.controller;


import com.lms.cmpe.model.Book;
import com.lms.cmpe.model.BookList;
import com.lms.cmpe.model.User;
import com.lms.cmpe.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Created by akash on 11/26/16.
 */

@Controller
public class LoginController {

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private BookService bookService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/")
    public String loginForm(Model model){
        User user = new User();
        model.addAttribute("user",user);
        return "login";
    }

    @PostMapping("/login")
    public String validateLogin(@ModelAttribute User user, Model model, HttpSession session){

        User result = userService.getUserByEmail(user.getEmail());
        if(result!=null && result.getPassword().equals(user.getPassword())){
            user = result;
            session.setAttribute("user",user);
            model.addAttribute("user",user);
                if(result.isVerified()) {

                    return "redirect:/profile";
                }
                else{
                    return "activate";
                }
        }
        return "login";
    }


    @GetMapping("/signup")
    public String signUpForm(Model model){
        User user = new User();
        model.addAttribute("user",user);
        return "signup";
    }

    @PostMapping("/signup")
    public String createUser(@ModelAttribute User user,@RequestParam(value="userRole", required=false) String role){
        user.setVerificationCode(Integer.toString(verificationService.verficationCode()));
        if(role != null){
            user.setUserRole(role);
        }

        userService.saveUser(user);
        mailService.sendMail(user);
        return "activate";
    }

    @PostMapping("/activate")
    public String activateAccount(@RequestParam(value="activationcode", required=true) String activationcode,
                                  @RequestParam(value="userid", required=true) String userid,
                                  Model model){
        System.out.println(userid);
        User user = userService.getUserById(Integer.parseInt(userid));
        model.addAttribute("user",user);
        if(activationcode.equals(user.getVerificationCode())){
            user.setVerified(true);
            userService.updateUser(user);
            return "profile";
        }

        model.addAttribute("message","Incorrect Verification Code! Try again");
        return "activate";
    }

    @GetMapping("/profile")
    public String profile(Model model,HttpSession session){

        if(session.getAttribute("user")!=null){

            model.addAttribute("user",session.getAttribute("user"));
            User tempuser = (User)session.getAttribute("user");
            // TODO: Akash replace -> mybooks from transaction table
            model.addAttribute("mybooks",transactionService.getBooksToBeReturned(tempuser.getUserId()));

            if(session.getAttribute("returnlist")==null){
                BookList returnlist = new BookList();
                session.setAttribute("returnlist",returnlist);
            }

            model.addAttribute("returnlist", session.getAttribute("returnlist"));

            return "profile";
        }
        return "redirect:/";
    }

    @GetMapping("/return/book/{id}")
    public String returnCart(@PathVariable("id") int id, @ModelAttribute BookList returnlist,
                                Model model, HttpSession session){
        returnlist = (BookList)session.getAttribute("returnlist");

        for(Book book:returnlist.getBookList()){
            if(book.getBookId() == id){
                return "redirect:/profile";
            }
        }
        returnlist.getBookList().add(bookService.getBookById(id));
        return "redirect:/profile";
    }

    @GetMapping("/cancelreturn/book/{id}/{index}")
    public String returnToLibrary(@PathVariable("id") int id, @ModelAttribute BookList returnlist
            ,@PathVariable("index") int index, HttpSession session){

        returnlist = (BookList)session.getAttribute("returnlist");
        returnlist.getBookList().remove(index);
        return "redirect:/profile";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }

}
