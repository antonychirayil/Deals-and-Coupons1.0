import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { AdminService } from '../admin.service';
import { coupon } from '../coupon';
import {Router} from "@angular/router";

@Component({
  selector: 'app-coupon',
  templateUrl: './coupon.component.html',
  styleUrls: ['./coupon.component.css']
})
export class CouponComponent implements OnInit {

  coupons: coupon[]= [];
  formValue!:FormGroup;
  userObj : coupon = new coupon("","","","","","");
  result: any;
  pcode: any;
  edate:any
  searchText: any;

  //Login
  // @ts-ignore
  public userDetails;

  constructor(private adminService:AdminService, private fb:FormBuilder,private router: Router) { }


  ngOnInit(): void {

    //login
    const storage = localStorage.getItem('google_auth');

    if (storage){
      this.userDetails=JSON.parse(storage);
    }else {
      this.signOut();
    }
    //login

    this.getCoupons()


    this.formValue=this.fb.group({
    id: [""],
    provider: [""],
    code: [""],
    category: [""],
    description: [""],
    expiryDate: [""],
    })




  }
  updatepcode(data: any,date:any){
    this.pcode=data;
    this.edate=date


  }

  getCoupons(){
    console.log('Test Call');
     this.adminService.getAllCoupons().subscribe(res=>{this.coupons=res});
}

//Signout
signOut():void{
  localStorage.removeItem('google_auth');
  this.router.navigateByUrl('/header').then();
}
//Signout
}

