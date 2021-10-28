import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { coupon } from './coupon';
@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor(private http:HttpClient) { }

  getAllCoupons(){
    return this.http.get<coupon[]>('http://localhost:8100/coupons/coupons/list');
  }

  public AddCoupon(c:coupon){
    return this.http.post<coupon>("http://localhost:8100/coupons/coupons/add",c);
  }

  public updateCoupon(user:coupon,id:String){
    return this.http.post<coupon>("http://localhost:8100/coupons/coupons/update/"+id,user);
  }

  public DeleteCoupon(id:String){
    return this.http.delete("http://localhost:8100/coupons/coupons/delete/"+id);
  }

}
