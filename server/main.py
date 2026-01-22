from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy import create_engine, Column, Integer, String, BigInteger
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session
from pydantic import BaseModel
from typing import List, Optional

# ==========================================
# 1. 데이터베이스 설정 (여기를 수정하세요!)
# ==========================================
# TiDB 정보 입력
TIDB_USER = "3GgBdtkMcjG6eNq.root"
TIDB_PASSWORD = "0uVXJM67nD5wSV3x"
TIDB_HOST = "gateway01.ap-northeast-1.prod.aws.tidbcloud.com"
TIDB_PORT = "4000"
TIDB_DB_NAME = "test"  # 아까 만든 test DB 이름 원래는 maindb임

# 접속 URL 만들기 (수정 X)
DATABASE_URL = f"mysql+pymysql://{TIDB_USER}:{TIDB_PASSWORD}@{TIDB_HOST}:{TIDB_PORT}/{TIDB_DB_NAME}?ssl_verify_cert=true&ssl_verify_identity=true"

# DB 엔진 생성 (TiDB는 보안 접속이 필수라 ssl 옵션이 중요합니다)
engine = create_engine(
    DATABASE_URL,
    connect_args={"ssl": {"ca": "/etc/ssl/certs/ca-certificates.crt"}} 
    # 윈도우라면 위 connect_args 줄을 지우고 실행해보세요. 안되면 알려주세요!
)

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()


# ==========================================
# 2. 데이터베이스 모델 (테이블) 정의
# ==========================================

# (1) 관리자 테이블 (manager)
class Manager(Base):
    __tablename__ = "managerdb"

    id = Column(Integer, primary_key=True, index=True)
    manager_id = Column(String(50), unique=True, index=True)
    m_password = Column(String(100))
    m_name = Column(String(50))
    phone_number = Column(String(20))
    manager_number = Column(String(20))

# (2) 상품 테이블 (product)
class Product(Base):
    __tablename__ = "productdb"

    p_id = Column(Integer, primary_key=True, index=True)
    p_name = Column(String(100))
    p_price = Column(Integer)
    p_quantity = Column(Integer)
    b_key = Column(BigInteger) # 바코드 번호가 길어서 BigInteger 사용
    category = Column(String(50))

# (3) 유저 테이블 (users)
class User(Base):
    __tablename__ = "usersdb"

    id = Column(Integer, primary_key=True, index=True)
    login_id = Column(String(50), unique=True, index=True)
    password = Column(String(100))
    name = Column(String(50))
    p_number = Column(String(20))

# 테이블이 없으면 자동 생성 (이미 있으면 무시함)
Base.metadata.create_all(bind=engine)


# ==========================================
# 3. Pydantic 스키마 (데이터 주고받는 양식)
# ==========================================

# Manager 스키마
class ManagerCreate(BaseModel):
    manager_id: str
    m_password: str
    m_name: str
    phone_number: str
    manager_number: str

class ManagerResponse(ManagerCreate):
    id: int
    class Config:
        orm_mode = True

# Product 스키마
class ProductCreate(BaseModel):
    p_name: str
    p_price: int
    p_quantity: int
    b_key: int
    category: str

class ProductResponse(ProductCreate):
    p_id: int
    class Config:
        orm_mode = True

# User 스키마
class UserCreate(BaseModel):
    login_id: str
    password: str
    name: str
    p_number: str

class UserResponse(UserCreate):
    id: int
    class Config:
        orm_mode = True


# ==========================================
# 4. API 서버 (엔드포인트)
# ==========================================
app = FastAPI()

# DB 세션 의존성 함수
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# --- 기본 확인용 ---
@app.get("/")
def read_root():
    return {"message": "졸업작품 API 서버가 정상 작동 중입니다!"}

# --- Manager API ---
@app.post("/managers/", response_model=ManagerResponse)
def create_manager(manager: ManagerCreate, db: Session = Depends(get_db)):
    db_manager = Manager(**manager.dict())
    db.add(db_manager)
    db.commit()
    db.refresh(db_manager)
    return db_manager

@app.get("/managers/", response_model=List[ManagerResponse])
def read_managers(skip: int = 0, limit: int = 100, db: Session = Depends(get_db)):
    return db.query(Manager).offset(skip).limit(limit).all()

# --- Product API ---
@app.post("/products/", response_model=ProductResponse)
def create_product(product: ProductCreate, db: Session = Depends(get_db)):
    db_product = Product(**product.dict())
    db.add(db_product)
    db.commit()
    db.refresh(db_product)
    return db_product

@app.get("/products/", response_model=List[ProductResponse])
def read_products(skip: int = 0, limit: int = 100, db: Session = Depends(get_db)):
    return db.query(Product).offset(skip).limit(limit).all()

# --- User API ---
@app.post("/users/", response_model=UserResponse)
def create_user(user: UserCreate, db: Session = Depends(get_db)):
    db_user = User(**user.dict())
    db.add(db_user)
    db.commit()
    db.refresh(db_user)
    return db_user

@app.get("/users/", response_model=List[UserResponse])
def read_users(skip: int = 0, limit: int = 100, db: Session = Depends(get_db)):
    return db.query(User).offset(skip).limit(limit).all()