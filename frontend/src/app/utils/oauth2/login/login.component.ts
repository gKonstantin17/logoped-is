import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../environments/environment';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit{
  constructor(private activatedRoute: ActivatedRoute,
              private router:Router,
              private http:HttpClient) {
  }
  ngOnInit(): void {

    console.log("LoginComponent - init");
    this.activatedRoute.queryParams.subscribe(params =>{
      if (params['code']) { // получаем authcode
        const code = params['code'];
        const state = params['state'];

        window.history.pushState({},"",document.location.href.split("?")[0]);

        this.requestTokens(code, state);
        return; // преврать бесконечный цикл
      }
      this.showAuthWindow();// если authcode не получили
    });
  }

  private showAuthWindow() { // цикл PKCE
    console.log("LoginComponent - showAuthWindow");
    const state = this.randomString(40);
    localStorage.setItem('state',state);

    const params = [
      'response_type=code', // for authcode
      'state=' + state,
      'client_id=' + environment.KC_CLIENT_ID,
      'scope=openid',

      'redirect_uri='+encodeURIComponent(environment.REDIRECT_URL)
    ];

    let url = environment.KC_URI+"/auth?"+params.join('&');
    window.open(url,'_self');
  }

  randomString(length:number) {
    let result = '';
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    const charsLength = characters.length;
    for (let i = 0; i < length; i++) {
      result += characters.charAt(Math.floor(Math.random() * charsLength));
    }
    return result;
  }

  private requestTokens(code: any, state: any) {
    if (!this.checkState(state)){
      return;
    }
    this.http.post(environment.BFF_URI+'/bff/token',code,{
      headers: {
        'Content-Type': 'application/json; charset=UTF-8'
      }
    }).subscribe({
      next: ((response:any) => {
        this.router.navigate(['/dashboard']);

      }),
      error: (error => {
        console.log(error);
      })
    })
  }

  private checkState(state: any) {
    if (state !== localStorage.getItem('state') as string) {
      console.log('Invalid state');
      return false;
    }
    localStorage.removeItem('state');
    return true;
  }
}
