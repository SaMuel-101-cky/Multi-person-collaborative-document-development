```mermaid
usecaseDiagram
    actor "注册用户" as User

    usecase "登录/登出" as UC1
    usecase "修改个人信息" as UC2
    usecase "创建文档" as UC3
    usecase "多人协作" as UC8
    usecase "自动保存" as UC10

    User --> UC1
    User --> UC2
    User --> UC3
    User --> UC8
    
    UC8 ..> UC10 : include
```
