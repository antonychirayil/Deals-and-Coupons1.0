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
}
