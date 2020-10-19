<template>
  <div id="base_application">
    <footer class="pb-4 bg-primary-3 text-light" id="footer">
      <div class="container">
        <div class="row mb-5">
          <div class="col-6 col-lg-3 col-xl-2">
            <h5>Navigate</h5>
            <ul class="nav flex-column">
              <li class="nav-item">
                <a class="nav-link">
                  <router-link to="/conference-application">Conference Application</router-link>
                </a>
              </li>
              <li class="nav-item">
                <a class="nav-link">
                  <router-link to="/conference-home">All Conferences</router-link>
                </a>
              </li>
            </ul>
          </div>
          <div class="col-6 col-lg">
            <h5>Contact</h5>
            <ul class="list-unstyled">
              <li class="mb-3 d-flex">
                <svg
                  class="icon"
                  width="24px"
                  height="24px"
                  viewBox="0 0 24 24"
                  version="1.1"
                  xmlns="http://www.w3.org/2000/svg"
                  xmlns:xlink="http://www.w3.org/1999/xlink"
                >
                  <title>Icon For Marker#1</title>
                  <g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                    <rect opacity="0" x="0" y="0" width="24" height="24" />
                    <path
                      d="M5,10.5 C5,6 8,3 12.5,3 C17,3 20,6.75 20,10.5 C20,12.8325623 17.8236613,16.03566 13.470984,20.1092932 C12.9154018,20.6292577 12.0585054,20.6508331 11.4774555,20.1594925 C7.15915182,16.5078313 5,13.2880005 5,10.5 Z M12.5,12 C13.8807119,12 15,10.8807119 15,9.5 C15,8.11928813 13.8807119,7 12.5,7 C11.1192881,7 10,8.11928813 10,9.5 C10,10.8807119 11.1192881,12 12.5,12 Z"
                      fill="#000000"
                      fill-rule="nonzero"
                    />
                  </g>
                </svg>
                <div class="ml-3">
                  <span>
                    220 Han Dan Rd
                    <br />Uptown, Lungmen
                  </span>
                </div>
              </li>
              <li class="mb-3 d-flex">
                <svg
                  class="icon"
                  width="24px"
                  height="24px"
                  viewBox="0 0 24 24"
                  version="1.1"
                  xmlns="http://www.w3.org/2000/svg"
                  xmlns:xlink="http://www.w3.org/1999/xlink"
                >
                  <title>Icon For Call#1</title>
                  <g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                    <rect opacity="0" x="0" y="0" width="24" height="24" />
                    <path
                      d="M11.914857,14.1427403 L14.1188827,11.9387145 C14.7276032,11.329994 14.8785122,10.4000511 14.4935235,9.63007378 L14.3686433,9.38031323 C13.9836546,8.61033591 14.1345636,7.680393 14.7432841,7.07167248 L17.4760882,4.33886839 C17.6713503,4.14360624 17.9879328,4.14360624 18.183195,4.33886839 C18.2211956,4.37686904 18.2528214,4.42074752 18.2768552,4.46881498 L19.3808309,6.67676638 C20.2253855,8.3658756 19.8943345,10.4059034 18.5589765,11.7412615 L12.560151,17.740087 C11.1066115,19.1936265 8.95659008,19.7011777 7.00646221,19.0511351 L4.5919826,18.2463085 C4.33001094,18.1589846 4.18843095,17.8758246 4.27575484,17.613853 C4.30030124,17.5402138 4.34165566,17.4733009 4.39654309,17.4184135 L7.04781491,14.7671417 C7.65653544,14.1584211 8.58647835,14.0075122 9.35645567,14.3925008 L9.60621621,14.5173811 C10.3761935,14.9023698 11.3061364,14.7514608 11.914857,14.1427403 Z"
                      fill="#000000"
                    />
                  </g>
                </svg>
                <div class="ml-3">
                  <span>PRTS port:00</span>
                  <span class="d-block text-muted text-small">Mon - Fri 9am - 5pm</span>
                </div>
              </li>
              <li class="mb-3 d-flex">
                <svg
                  class="icon"
                  width="24px"
                  height="24px"
                  viewBox="0 0 24 24"
                  version="1.1"
                  xmlns="http://www.w3.org/2000/svg"
                  xmlns:xlink="http://www.w3.org/1999/xlink"
                >
                  <title>Icon For Mail</title>
                  <g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                    <rect opacity="0" x="0" y="0" width="24" height="24" />
                    <path
                      d="M5,6 L19,6 C20.1045695,6 21,6.8954305 21,8 L21,17 C21,18.1045695 20.1045695,19 19,19 L5,19 C3.8954305,19 3,18.1045695 3,17 L3,8 C3,6.8954305 3.8954305,6 5,6 Z M18.1444251,7.83964668 L12,11.1481833 L5.85557487,7.83964668 C5.4908718,7.6432681 5.03602525,7.77972206 4.83964668,8.14442513 C4.6432681,8.5091282 4.77972206,8.96397475 5.14442513,9.16035332 L11.6444251,12.6603533 C11.8664074,12.7798822 12.1335926,12.7798822 12.3555749,12.6603533 L18.8555749,9.16035332 C19.2202779,8.96397475 19.3567319,8.5091282 19.1603533,8.14442513 C18.9639747,7.77972206 18.5091282,7.6432681 18.1444251,7.83964668 Z"
                      fill="#000000"
                    />
                  </g>
                </svg>
                <div class="ml-3">
                  <a href="#">arkchair@arknights.io</a>
                </div>
              </li>
            </ul>
          </div>
        </div>

        <div class="justify-content-center">
          <div class="col col-md-auto text-center">
            <small class="text-muted">
              &copy;2020 Proudly delivered by Xuan and his teammates with
              <a
                href="https://cn.vuejs.org/"
              >Vue.js</a> and
              <a href="https://spring.io/projects/spring-boot">Spring Boot.</a>
            </small>
          </div>
          <div class="col col-md-auto text-center">  
            <small class="text-muted">
              This is a class project of Software Engineering, Fudan University. Pictures on this site are from <a
                href="http://ak.mooncell.wiki/w/%E9%A6%96%E9%A1%B5"
              >PRTS</a>.
            </small>
          </div>
          <div class="col col-md-auto text-center">  
            <small class="text-muted">
              The administrator account is <code>admin</code>; the password is <code>password</code>.
            </small>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: "footerbar",
  data() {
    return {

    }
  },
  methods: {

  }
};
</script>