FROM nginx:1.27-alpine

# 기본 설정 파일 삭제 후 우리가 만든 설정들 복사
COPY nginx/nginx.conf /etc/nginx/nginx.conf
COPY nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf

EXPOSE 80