# Backend-AI

โปรเจคตัวอย่าง Spring Boot (SQLite) สำหรับระบบสมาชิกที่มีการโอนแต้มระหว่างผู้ใช้

สรุป
- ภาษา: Java, Spring Boot
- เบสข้อมูล: SQLite (ไฟล์ data.db)
- จุดประสงค์: ตัวอย่างการสมัคร/ล็อกอิน (JWT) และฟีเจอร์โอนคะแนน (points transfer)

Prerequisites
- JDK 17+
- Maven
- macOS (ทดสอบบนระบบผู้ใช้)

Build และรัน
1. สร้างไฟล์ jar:
   mvn -DskipTests package
2. รันแอป:
   java -jar target/backend-ai-0.0.1-SNAPSHOT.jar
3. แอปจะใช้พอร์ต 8080 ตามค่าเริ่มต้น

ไฟล์สำคัญ
- src/main/resources/schema.sql  — โครงสร้างตาราง users และ transfers
- data.db                     — ไฟล์ SQLite (runtime)
- .gitignore                  — กำหนดให้ /target, data.db, app.log ถูกละเว้นจาก git

API (หลัก)
- POST /register
  - สมัครสมาชิก (email, password, firstname, lastname, phone, birthday, ...)

- POST /login
  - คืนค่า JWT: {"token":"..."}

- GET /me
  - คืนข้อมูลผู้ใช้ที่ authenticate (ต้องใส่ Authorization: Bearer <token>)

- POST /transfer
  - โอนแต้มระหว่างผู้ใช้ (ต้อง authenticate)
  - Payload (JSON):
    - toUserId (Long) หรือ toMemberCode (String) — ระบุผู้รับอย่างใดอย่างหนึ่ง
    - amount (Integer) — จำนวนแต้มที่ต้องการโอน (บวก)
    - note (String) — ข้อความบันทึก (optional)
  - ตัวอย่าง:
    {
      "toMemberCode": "MEM12345",
      "amount": 100,
      "note": "ของขวัญ"
    }
  - พฤติกรรม:
    - ตรวจยอดผู้ส่งว่าพอหรือไม่ (จะไม่อนุญาตหาก insufficient)
    - ห้ามโอนให้ตัวเอง
    - เป็น transaction: deduct จากผู้ส่ง แล้วเพิ่มให้ผู้รับ จากนั้นบันทึก row ในตาราง transfers
  - Response: 201 Created กับข้อมูล transfer ที่บันทึก

- GET /transfers?limit=N
  - คืนรายการโอนล่าสุดของผู้ส่ง (ต้อง authenticate)

ตัวอย่างคำสั่ง curl
- สมัคร
  curl -X POST -H "Content-Type: application/json" -d '{"email":"you@example.com","password":"pw","firstname":"A","lastname":"B"}' http://localhost:8080/register

- เข้าสู่ระบบ
  curl -X POST -H "Content-Type: application/json" -d '{"email":"you@example.com","password":"pw"}' http://localhost:8080/login

- โอนแต้ม (ใช้ token จาก /login)
  curl -X POST -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" -d '{"toMemberCode":"MEM123","amount":50,"note":"Thanks"}' http://localhost:8080/transfer

ทดสอบ / debug
- ดู log: app.log
- ตรวจดูฐานข้อมูล: sqlite3 data.db
- OpenAPI: http://localhost:8080/v3/api-docs
- Swagger UI: http://localhost:8080/swagger-ui/index.html (ถ้ามี)

ข้อควรพิจารณา / ข้อเสนอแนะ
- เพิ่ม validation ให้เข้มขึ้น (เช่น limit/threshold ของการโอน)
- รองรับ refresh token, rate limiting, และ auditing/notification
- ทดสอบ concurrent transfer เพื่อป้องกัน race condition (ปัจจุบันทำแบบอ่านแล้วเขียน; ถ้าจำเป็นควรใช้ locking หรือ SQL atomic update)

License
- MIT (ปรับใช้ตามต้องการ)
