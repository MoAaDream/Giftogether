# 선물 위시리스트 - Giftogether

<img src="https://github.com/MoAaDream/Giftogether/assets/87762815/281f7352-7e3b-4940-9294-c1c049c4769d" width="400" height="400"/>

### 에어팟을 잃어버린 김아무개

- 생일이 다가오는데 카카오톡 위시리스트에 추가할만큼 마음에 드는 위시가 없다. 최근에 에어팟프로2를 잃어버려서 다시 사야하는데 너무 고가라서 한명에게 선물 받기는 취준생 입장에서 부담스러운 상황이다.
  친구들이 돈을 모아서 선물해주면 좋겠다만, 그걸 주최해줄 친구는 없다. 그렇다고 바로 계좌로 돈을 받기에는 민망한 상황. 나 대신 돈을 모아서 주문해주는 서비스가 있으면 좋겠다. 생일인 만큼 축하 메세지도 함께
  받고 싶다!

### 기능

- 선물해주고 싶은 사람을 위한 위시리스트 생성
- 내가 원하는 선물 위시리스트 생성
- 기념일 당일 선물 메세지 확인 가능
- 모금 달성 시 등록된 이메일로 알림 전송
- 모금 달성하지 못한 선물
    - 부족한 금액 결제 후 선물 수령
    - 모금한 금액 계좌로 받기

## Page 소개

### Login Page

<img src="https://github.com/MoAaDream/Giftogether/assets/87762815/82689611-c390-4256-a19f-d1db6c264f54" width="400" height="800"/>

### Main Page

<img src="https://github.com/MoAaDream/Giftogether/assets/87762815/2cc6d682-6913-41b4-ad80-7415e7818a0d" width="400" height="800"/>

### My Page

<img src="https://github.com/MoAaDream/Giftogether/assets/87762815/65faa6d7-bae2-424f-84a5-2a48b93f08ec" width="400" height="800"/>

### Wishlist Page

<img src="https://github.com/MoAaDream/Giftogether/assets/87762815/19b8717f-a3cb-4e92-ae39-330bea37027c" width="400" height="800"/>

### Product Page

<img src="https://github.com/MoAaDream/Giftogether/assets/87762815/4053af6f-7ef4-4b79-b4f4-832a03d478ab" width="400" height="800"/>

### Product Detail Page

<img src="https://github.com/MoAaDream/Giftogether/assets/87762815/5f9bb038-4818-477d-8f23-1ed830c630f3" width="400" height="700"/>

### Donate Page

<img src="https://github.com/MoAaDream/Giftogether/assets/87762815/19dd21f0-5044-4995-a046-d661bc048b43" width="400" height="600"/>

### Payment Page

<img src="https://github.com/MoAaDream/Giftogether/assets/87762815/6ad8101d-73cd-4926-94d2-880ce945fcf2" width="400" height="400"/>

## 🛠️ Tech Skill

<hr>

|                                                   Frontend                                                    |                                                                                                                                                                       Backend                                                                                                                                                                       |                                                                                                                                                                                                                Infrastructure and Tools                                                                                                                                                                                                                |
|:-------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| <img src="https://img.shields.io/badge/javascript-F7DF1E?style=flat-square&logo=javascript&logoColor=black"/> | <img src="https://img.shields.io/badge/springboot-6DB33F?style=flat-square&logo=springboot&logoColor=black"/><br><img src="https://img.shields.io/badge/springsecurity-6DB33F?style=flat-square&logo=springsecurity&logoColor=black"/><br><img src="https://img.shields.io/badge/oracle-F80000?style=flat-square&logo=oracle&logoColor=black"/><br> | <img src="https://img.shields.io/badge/amazons3-FE5F50?style=flat-square&logo=amazons3&logoColor=black"/><br><img src="https://img.shields.io/badge/apachejmeter-D22128?style=flat-square&logo=apachejmeter&logoColor=black"/><br><img src="https://img.shields.io/badge/notion-181818?style=flat-square&logo=notion&logoColor=whtie"/><br><img src="https://img.shields.io/badge/postman-FF6C37?style=flat-square&logo=postman&logoColor=black"/><br> |

## ERD

![Moadream](https://github.com/MoAaDream/Giftogether/assets/87762815/7a8de859-7d25-4ed4-b0be-73e3b8ff519f)

<br>

## Install

### 1. Clone Repository

Github

```git
git clone https://github.com/MoAaDream/Giftogether.git
```

### 2. Properties Setting

properties

```
# Oracle prod Cloud
ORACLE_URL= 
ORACLE_USERNAME=
ORACLE_PASSWORD= 

# kakao
spring.security.oauth2.client.registration.kakao.client-id=
spring.security.oauth2.client.registration.kakao.client-secret=
spring.security.oauth2.client.registration.kakao.client-authentication-method=

spring.security.oauth2.client.registration.kakao.scope=
spring.security.oauth2.client.registration.kakao.redirect-uri=
spring.security.oauth2.client.registration.kakao.authorization-grant-type=
spring.security.oauth2.client.registration.kakao.client-name=

spring.security.oauth2.client.provider.kakao.authorization-uri=
spring.security.oauth2.client.provider.kakao.token-uri=
spring.security.oauth2.client.provider.kakao.user-info-uri=
spring.security.oauth2.client.provider.kakao.user-info-authentication-method=
spring.security.oauth2.client.provider.kakao.user-name-attribute=

# aws s3 key
cloud.aws.credentials.accessKey=
cloud.aws.credentials.secretKey=
cloud.aws.stack.auto=

# AWS S3 Service bucket
cloud.aws.s3.bucket=
cloud.aws.region.static=

# AWS S3 Bucket URL
cloud.aws.s3.bucket.url=

# Cron
myCustom.cron=

# PortOne
PORTONE_CODE=
PORTONE_KEY=
PORTONE_SECRET=

# Mail
MAIL_HOST=
MAIL_PORT=
MAIL_USERNAME=
MAIL_PASSWORD=

```

### 3. build

```
./gradlew bootRun
```

## Member

<hr>

### CONTACT

<table width="1000">
    <thead>
    </thead>
    <tbody>
    <tr>
        <th>Name</th>
        <td width="100" align="center">이경훈</td>
        <td width="100" align="center">김가은</td>
        <td width="100" align="center">장희선</td>
        <td width="100" align="center">전민재</td>
    </tr>
    <tr>
        <th>Profile</th>
        <td width="100" align="center">
            <a href="https://github.com/TinyFrogs">
                <img src="https://avatars.githubusercontent.com/u/87762815?v=4" width="60" height="60">
            </a>
        </td>
        <td width="100" align="center">
            <a href="https://github.com/gganddabbiya">
                <img src="https://avatars.githubusercontent.com/u/90741039?v=4" width="60" height="60">
            </a>
        </td>
        <td width="100" align="center">
            <a href="https://github.com/Jade9846">
                <img src="https://avatars.githubusercontent.com/u/101115635?v=4" width="60" height="60">
            </a>
        </td>
        <td width="100" align="center">
            <a href="https://github.com/m-jeon">
                <img src="https://avatars.githubusercontent.com/u/90237802?v=4" width="60" height="60">
            </a>
        </td>
    </tr>
    <tr>
        <th>Role</th>
        <td width="150" align="center">
            Leader<br>
            Backend<br>
            Frontend<br>
        </td>
        <td width="150" align="center">
            Backend<br>
            Frontend<br>
        </td>
        <td width="150" align="center">
            Backend<br>
            Frontend<br>
        </td>
        <td width="150" align="center">
            Backend<br>
            Frontend<br>
        </td>
    </tr>
    <tr>
        <th>GitHub</th>
        <td width="100" align="center">
            <a href="https://github.com/TinyFrogs">
                Github
            </a>
        </td>
        <td width="100" align="center">
            <a href="https://github.com/gganddabbiya">
                Github
            </a>
        </td>
        <td width="100" align="center">
            <a href="https://github.com/Jade9846">
                Github
            </a>
        </td>
        <td width="100" align="center">
            <a href="https://github.com/m-jeon">
                Github
            </a>
        </td>
    </tr>
    </tbody>
</table>



