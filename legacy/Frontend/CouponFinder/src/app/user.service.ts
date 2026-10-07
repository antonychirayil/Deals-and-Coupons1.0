import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from './user';

@Injectable({
  providedIn: 'root'
})

export class UserService {

  constructor(private _http:HttpClient) {
  }

  // public loginUser(user: User):Observable<any>{
  //   return this._http.post<any>("http://localhost:8082/login",user)

  // }

  public getUser(useremail:String):Observable<any>{
    return this._http.get<User>("http://localhost:8082/getuser/"+useremail)
  }

  public registerUser(user: User):Observable<any>{
    return this._http.post<any>("http://localhost:8082/register",user)
  }
}
