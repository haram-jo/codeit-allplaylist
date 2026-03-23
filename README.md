# 🎬 ALL PLAYLIST (모두의 플레이리스트)
> **여러 OTT에 흩어져 있는 콘텐츠를 취향대로 묶고 공유하는 플랫폼**

[![Java](https://img.shields.io/badge/Java-17-007396?logo=java&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-FF4438?logo=redis&logoColor=white)](https://redis.io/)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-231F20?logo=apachekafka&logoColor=white)](https://kafka.apache.org/)

---

## 📅 프로젝트 개요
- **진행 기간**: 2025년 12월 18일 ~ 2026년 1월 29일
- **주요 목적**: 분산된 미디어 콘텐츠의 큐레이션 및 사용자 간 취향 공유 플랫폼 개발
- **핵심 성과**: 
  - **성능 개선**: Redis 도입을 통해 인증 처리량 약 **9배 향상**
  - **실시간성**: Kafka & SSE 연동을 통한 비동기 실시간 알림 시스템 구축
  - **최적화**: 대용량 데이터 환경에서 UUID 보조 커서를 활용한 정렬 및 무한 로딩 최적화

---

## 🛠 기술 스택

### Backend & Infrastructure
- **Language**: Java 17
- **Framework**: Spring Boot, Spring JPA, Spring Security, Spring Batch, QueryDSL
- **Database**: MySQL (Amazon RDS), Redis (Amazon ElastiCache)
- **Messaging**: Kafka (Confluent Cloud)
- **DevOps**: AWS ECS (Fargate), ECR, S3, Route 53, ALB, GitHub Actions, Docker

![기술 스택](https://github.com/user-attachments/assets/86ea8dd6-e2ee-4b7c-bd93-ca4d2dd63c29)

---

## 🏗 시스템 아키텍처

### Cloud Design
- **분산 환경 설계**: Gateway(Nginx), API Service, Batch Service로 모듈을 분리하여 시스템 독립성 및 확장성 확보
- **비동기 이벤트 기반 알림**: `Producer(구독 이벤트)` -> `Kafka` -> `Consumer` -> `SSE`를 통한 실시간 알림 전달

![클라우드 설계](https://github.com/user-attachments/assets/157f3751-0b86-41dd-b578-bb9f1ec73ae2)

### Database ERD
![ERD 다이어그램](https://github.com/user-attachments/assets/ddf8af0e-3d33-4eab-903f-0e071e0e54ce)

---

## ✨ 주요 기능
- **콘텐츠 큐레이션**: TMDB API 연동으로 최신 콘텐츠 정보를 자동 갱신하고, 사용자 맞춤형 플레이리스트 생성 및 관리 기능을 제공합니다.
- **실시간 상호작용**: 팔로우, 구독, 콘텐츠 추가 등 이벤트 발생 시 즉각적인 실시간 알림을 수신합니다.
- **검색 및 정렬**: 최신순, 구독순, 평점순 등 복합적인 필터링을 통해 원하는 콘텐츠를 빠르게 탐색할 수 있습니다.

---

## 🚀 트러블 슈팅 및 성능 최적화

### 1. Redis 기반 Refresh Token 관리로 인증 성능 개선
- **Issue**: 기존 RDB(MySQL) 저장 방식 사용 시, 동시 접속자 증가에 따른 I/O 병목으로 에러율 3% 발생.
- **Solution**: In-Memory DB인 Redis로 저장소를 전환하고 TTL(Time-To-Live)을 적용하여 데이터베이스 부하 분산.
- **Result**: 
  - 평균 응답 속도 개선 (**0.06초 → 0.01초**)
  - 처리량(TPS) 약 9배 증가 (**1,000명 → 9,800명**)
  - 동시 접속 시 **에러율 0%** 달성

### 2. 정렬 지표 중복으로 인한 무한 로딩 및 데이터 중복 노출 해결
- **Issue**: 정렬 값이 동일한 데이터가 많을 경우, 페이징 처리(No-offset) 시 데이터가 누락되거나 중복 노출되는 현상 발생.
- **Solution**: 고유값인 **UUID를 보조 커서로 활용**한 2차 비교 로직을 추가하고, DB 인덱스 탐색 방향을 일치화함.
- **Result**: 페이징 처리의 정확도를 확보하고 데이터베이스 탐색 효율을 극대화하여 사용자 경험 개선.

---

## 📈 모니터링 및 품질 관리
- **모니터링**: Prometheus와 Grafana를 연동하여 Batch 실행 이력, JVM 힙 메모리 상태, DB 커넥션 풀 등의 핵심 지표를 실시간 시각화하여 관리합니다.
- **코드 품질**: CodeRabbit AI를 통한 자동 코드 리뷰 시스템을 구축하고, 통일된 PR 템플릿을 도입하여 협업 효율과 코드 퀄리티를 유지합니다.
