import Vue from 'vue'
import Router from 'vue-router'
import { Message } from 'element-ui';

import Login from '@/pages/Login'
import Register from '@/pages/Register'
import ConferenceApplication from '@/pages/ConferenceApplication'
import ConferenceVerification from '@/pages/ConferenceVerification'
import ConferenceHome from '@/pages/ConferenceHome'
import ConferenceDetail from '@/pages/ConferenceDetail'
import PaperDetail from '@/pages/PaperDetail'
import Index from '@/pages/Index'
import MessageInbox from '@/pages/MessageInbox'
import MyArkChair from '@/pages/MyArkChair'

import store from '../store'

Vue.use(Router)

export const router = new Router({
    mode: 'history',
    routes: [{
            path: '/login',
            name: 'Login',
            component: Login
        },
        {
            path: '/register',
            name: 'Register',
            component: Register
        },
        {
            path: '/conference-application',
            name: 'ConferenceApplication',
            component: ConferenceApplication,
            meta: {
                requireAuth: true, // 需要登录权限
                authRole: 'USER' // 管理员不能访问
            }
        },
        {
            path: '/',
            name: 'Index',
            component: Index
        },
        {
            path: '/verification',
            name: 'ConferenceVerification',
            component: ConferenceVerification,
            meta: {
                requireAuth: true,
                authRole: 'ADMIN' // 只有管理员可以访问
            }
        },
        {
            path: '/conference-home',
            name: 'ConferenceHome',
            component: ConferenceHome,
            meta: {
                requireAuth: true,
            }
        },
        {
            path: '/conference-detail/:conferenceID',
            name: 'ConferenceDetail',
            component: ConferenceDetail,
            meta: {
                requireAuth: true
            }
        },
        {
            path: '/message-inbox',
            name: 'MessageInbox',
            component: MessageInbox,
            meta: {
                requireAuth: true,
                authRole: 'USER'
            }
        },
        {
            path: '/my-ark-chair',
            name: 'MyArkChair',
            component: MyArkChair,
            meta: {
                requireAuth: true,
                authRole: 'USER'
            }
        },
        {
            path: '/paper/:paperID',
            name: 'PaperDetail',
            component: PaperDetail,
            meta: {
                requireAuth: true,
                authRole: 'USER'
            }
        },
    ]
})

// 前端登录拦截
router.beforeEach(function(to, from, next) {
    if (to.matched.some(record => record.meta.requireAuth)) {
        // 如果需要登录
        if (store.state.token) {
            // 如果需要对用户权限有要求
            if (to.matched.some(record => record.meta.authRole)) {
                if (store.state.userType == to.meta.authRole) {
                    next()
                } else {
                    next('/');
                    Message.warning({
                        type: 'warning',
                        center: true,
                        dangerouslyUseHTMLString: true,
                        message: "<strong style='color:teal'>Sorry! You don't have the authority!</strong>"
                    })
                }
            } else {
                next();
            }
        } else {
            next({
                path: '/login',
                query: { redirect: to.fullPath } // 登录成功之后重新跳转到该路由
            })
        }
    } else {
        next()
    }
})