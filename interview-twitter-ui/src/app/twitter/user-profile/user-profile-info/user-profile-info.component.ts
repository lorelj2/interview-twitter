import {Component, Input} from '@angular/core';
import {UserProfileModel} from "../../../models/userprofile.model";

@Component({
  selector: 'app-user-profile-info',
  templateUrl: './user-profile-info.component.html',
  styleUrls: ['./user-profile-info.component.css']
})
export class UserProfileInfoComponent {

  @Input() userProfile: UserProfileModel;

}
