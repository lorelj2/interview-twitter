import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {UserProfileModel} from "../../models/userprofile.model";
import {Observable} from "rxjs/Observable";

const ENDPOINT_BASE = '/api/profile';

@Injectable()
export class UserProfileService {

  constructor(private http: HttpClient) {
  }

  fetchForUser(username: string) {
    return this.http.get<UserProfileModel>(ENDPOINT_BASE + '/' + username);
  }


}
