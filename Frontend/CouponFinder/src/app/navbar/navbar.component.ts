import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from 'src/app/user';
import { UserService } from 'src/app/user.service';


@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  user = new User();
  msg =''
  invalidLogin = false;
  formValue!:FormGroup;
  loginForm!:FormGroup;
  public totalItem : number = 0;
  public searchTerm !: string;
  loginusers = new User();
  constructor(private userservice : UserService, private router:Router ,private fb:FormBuilder) { }
  // constructor(private userservice : UserService, private router:Router ) { }

  ngOnInit(): void {
    this.loginForm=this.fb.group(
      {
        useremail:[""],
        password:[""],
      }
    )

  }

  // login(){
  //   if(this.userservice.loginUser(this.user)){
  //     this.userservice.loginUser(this.user).subscribe(
  //       (data:User)=>{
  //         alert("Sign in successfully")
  //         this.user=data;
  //         this.router.navigate(['/admin-dashboard']);
  //         //this.redirect();
  //       }

  //     );}
  //   else{
  //     alert("Invalid Login Credentials");
  //     this.msg="Enter valid email and password";
  //   }
  // }
  login(){
    this.userservice.getUser(this.loginForm.value.useremail).subscribe(
      res=>{
        console.log('response'+res);
        this.user=res;
        if(this.user.password==this.loginForm.value.password){
          alert("Sign in successfully")
          this.router.navigate(['/admin-dashboard']);
        }else{
          alert("Invalid Login Credentials");
          this.msg="Enter valid email and password";
        }
      }

    )

  }

  registerUser(){
    this.userservice.registerUser(this.user).subscribe(
      data =>{
        alert("Successfully registered");

        this.msg= "registration success"
      },
      error =>{
        alert("Exception occured: Admin Already Registered");
        this.msg=error.error;

      }

    )
  }



  //  redirect(){

  //     if(this.user.useremail=="Admin@gmail.com" && this.user.password==123456789){
  //      sessionStorage.setItem('role','admin');
  //      sessionStorage.setItem('useremail',String(this.user.useremail));
  //      this.invalidLogin=false;
  //      this.router.navigate(['']).then(()=>{
  //        window.location.reload();
  //      })
  //    }
  //    else {
  //     sessionStorage.setItem('role','customer');
  //     sessionStorage.setItem('useremail',String(this.user.useremail));
  //     this.invalidLogin=false;
  //     this.router.navigate(["/cart"]).then(()=>{
  //       window.location.reload();
  //     });

  //   }

  //  }

}
