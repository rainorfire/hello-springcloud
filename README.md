# hello-springcloud

## 认证服务器
1. 测试步骤：
    - 访问受保护资源请求：http://localhost:8852/api/private/echo/123
    - 跳转登录页面，输入登录名密码授权登录
    - 登录成功后，跳转到授权页面：http://localhost:8852/oauth/authorize?response_type=code&client_id=MY_APP_ID&scope=all&redirect_uri=http://localhost:8852/api/public/auth/callback
    - 授权后，会获取到授权码并重定向到指定请求地址：http://localhost:8852/api/public/auth/callback?code=YrgPUI
    - 在该请求内部，用HttpClientUtil访问请求：http://localhost:8852/oauth/token?grant_type=authorization_code&client_id=MY_APP_ID&client_secret=123456&redirect_uri=http://localhost:8852/api/public/auth/callback&scope=all&code=授权码 得到访问token，结果如下：
        - ``` {"access_token":"jwt Code","token_type":"bearer","refresh_token":"jwt Code","expires_in":54,"scope":"all","userName":"chenyj1","roleList":[{"authority":"ADMIN"},{"authority":"WORKER"}],"jti":"fd742f08-2c81-4f9b-a50e-94007af672d7"} ```
    - 再访问受保护资源请求：http://localhost:8852/api/private/echo/123，请求头携带access_token，如 Bear Jwt Code
    - 刷新access code 请求：http://localhost:8852/oauth/token?grant_type=refresh_token&refresh_token=刷新码&client_id=MY_APP_ID&client_secret=123456 重新得到访问token，结果如上所示
        
