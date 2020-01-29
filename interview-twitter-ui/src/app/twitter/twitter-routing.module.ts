import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MainComponent} from "./main/main.component";

const routes: Routes = [
  {path: '', redirectTo: 'app', pathMatch: 'full'},
  {
    path: 'app', component: MainComponent,
    children: [
      {path: '', redirectTo: 'tweets', pathMatch: 'full'},
      {path: 'tweets', loadChildren: 'app/twitter/tweets/tweets.module#TweetsModule'},
      {path: 'tweets/:username', loadChildren: 'app/twitter/user-tweets/user-tweets.module#UserTweetsModule'},
      {path: 'profile/:username', loadChildren: 'app/twitter/user-profile/user-profile.module#UserProfileModule'},

    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TwitterRoutingModule {
}
