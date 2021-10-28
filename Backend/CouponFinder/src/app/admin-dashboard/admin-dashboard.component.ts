import { Component, OnInit } from '@angular/core';
import { AdminService } from '../admin.service';
import { coupon } from '../coupon';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {

  constructor(private adminService:AdminService) { }
  coupons: coupon[]= [];

  ngOnInit(): void {
    this.getCoupons()
  }


  getCoupons(){
    console.log('Test Call');
     this.adminService.getAllCoupons().subscribe(res=>{this.coupons=res});
}
}
