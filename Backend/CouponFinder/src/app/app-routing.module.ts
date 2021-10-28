import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { CouponComponent } from './coupon/coupon.component';
import { HeaderComponent } from './header/header.component';

const routes: Routes = [
  {path:"header", component:HeaderComponent},{path:"coupon",component:CouponComponent},{path:"admin-dashboard",component:AdminDashboardComponent},
  {
    path:"",
    redirectTo:"header",
    pathMatch:"full"
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
