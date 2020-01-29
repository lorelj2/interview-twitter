import {Component, OnInit} from '@angular/core';
import {UserProfileModel} from "../../../models/userprofile.model";
import {UserProfileService} from "../../../services/userprofile/userprofile.service";
import {Observable} from "rxjs/Observable";
import {ActivatedRoute, Params} from "@angular/router";

@Component({
  selector: 'app-user-profile-view',
  templateUrl: './user-profile-view.component.html',
  styleUrls: ['./user-profile-view.component.css']
})
export class UserProfileViewComponent implements OnInit {

  $userProfile: Observable<UserProfileModel>;
  userName: string;

  constructor(private userProfileService: UserProfileService, private activatedRoute: ActivatedRoute) {
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.userName = params['username'];
      this.$userProfile = this.userProfileService.fetchForUser(this.userName);
    });
  }
}
