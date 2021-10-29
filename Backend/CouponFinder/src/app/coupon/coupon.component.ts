import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { AdminService } from '../admin.service';
import { coupon } from '../coupon';
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

  constructor(private adminService:AdminService, private fb:FormBuilder) { }


  ngOnInit(): void {

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
}

