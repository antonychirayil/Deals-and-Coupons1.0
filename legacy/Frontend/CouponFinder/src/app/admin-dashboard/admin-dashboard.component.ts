import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { AdminService } from '../admin.service';
import { coupon } from '../coupon';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {

  coupons: coupon[]= [];

  formValue!:FormGroup;

  userObj : coupon = new coupon("","","","","","");
  result: any;
  searchText: any; //initialise for search


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
  addCoupon(){
    console.log("in add coupon method");
    this.userObj.id=this.formValue.value.id;
    this.userObj.provider=this.formValue.value.provider;
    this.userObj.code=this.formValue.value.code;
    this.userObj.category=this.formValue.value.category;
    this.userObj.description=this.formValue.value.description;
    this.userObj.expiryDate=this.formValue.value.expiryDate;

    this.adminService.AddCoupon(this.userObj).subscribe(res=>{this.coupons.push(res)});
    console.log("Coupon Added successfully");
    this.ngOnInit();
  }

  editCoupon(data:any){
    this.formValue.controls['id'].setValue(data.id);
    this.formValue.controls['provider'].setValue(data.provider);
    this.formValue.controls['code'].setValue(data.code);
    this.formValue.controls['category'].setValue(data.category);
    this.formValue.controls['description'].setValue(data.description);
    this.formValue.controls['expiryDate'].setValue(data.expiryDate);

  }

  updateCoupon(){
    console.log("in add  method");
    this.userObj.id=this.formValue.value.id;
    this.userObj.provider=this.formValue.value.provider;
    this.userObj.code=this.formValue.value.code;
    this.userObj.category=this.formValue.value.category;
    this.userObj.description=this.formValue.value.description;
    this.userObj.expiryDate=this.formValue.value.expiryDate;

    this.adminService.updateCoupon(this.userObj,this.userObj.id).subscribe(res=>{
      console.log("updated coupon successfully")
    });
  }

  deleteCoupon(id:String){

    console.log("in delete method")
    this.adminService.DeleteCoupon(id).subscribe(res=>{this.result=res});
   console.log("deleted User")
   alert("deleted Coupon sucessfully");
    this.ngOnInit();


  }

  getCoupons(){
    console.log('Test Call');
     this.adminService.getAllCoupons().subscribe(res=>{this.coupons=res});
}

SignOutadmin(){
  alert("Signout Sucessfully")
}

}

