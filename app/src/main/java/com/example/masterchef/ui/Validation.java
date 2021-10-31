package com.example.masterchef.ui;

import android.util.Patterns;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class Validation {

   public boolean nameValidation(EditText editText, TextInputLayout textInputLayout){
       String value = editText.getText().toString();
       String noWhiteSpace = "(?=\\s+$)";
       if (value.isEmpty()){
           textInputLayout.setError("Enter your Name");
           return false;
       }else if (value.length() >=15){
           textInputLayout.setError("Username too long");
           return false;
       }else if (!value.matches(noWhiteSpace)){
           textInputLayout.setError("White spaces are not allowed");
            return false;
       }
       else{
           textInputLayout.setError(null);
           textInputLayout.setErrorEnabled(false);
           return true;
       }
   }

   public boolean emailValidation(EditText editText,TextInputLayout textInputLayout){
       String value = editText.getText().toString();
       if (value.isEmpty()){
           textInputLayout.setError("Enter An Email");
           return false;
       }else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()){
           textInputLayout.setError("Enter valid email address");
           return false;
       }else {
           textInputLayout.setError(null);
           textInputLayout.setErrorEnabled(false);
           return true;
       }
   }

   public boolean passwordValidation(EditText editText,TextInputLayout textInputLayout){
       String value = editText.getText().toString();
       String  passwordVal = "^" +
//               "(?=.*[0-9])" + //at least one digit
//               "(?=.*[a-z])" + //at least one lower case letter
//               "(?=.*[A-Z])" + //at least one Upper Case letter
               "(?=.*[a-zA-Z])" + //any letter
               "(?=.*[@#$%^&+=])" + //at least one special character
               "(?=\\s+$)" +  // no white space
               ".{4,}" + //at least 4 digit
               "$";
       if (value.isEmpty()){
           textInputLayout.setError("Enter a password");
           return false;
       }else if (!value.matches(passwordVal)){
           textInputLayout.setError("password is too weak");
           return false;
       }else {
           textInputLayout.setError(null);
           textInputLayout.setErrorEnabled(false);
           return true;
       }
   }
   public boolean passwordConfirmationValidation(EditText editText1,EditText editText2,TextInputLayout textInputLayout){
       String value1 = editText1.getText().toString();
       String value2 = editText2.getText().toString();

       if (!value1.contentEquals(value2))
       {
           textInputLayout.setError("Password Did not match");
           return false;
       }else {
           textInputLayout.setError(null);
           textInputLayout.setErrorEnabled(false);
           return true;
       }

   }


}
