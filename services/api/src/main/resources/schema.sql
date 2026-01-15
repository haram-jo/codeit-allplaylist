-- ✅ h2 DB는 서버를 껐다가 키면 데이터가 매번 증발하는 형식이다.✅ --
-- ✅ 기존 테이블 삭제 (초기화용)
DROP TABLE IF EXISTS content_tags CASCADE;
DROP TABLE IF EXISTS tags CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS playlist_contents CASCADE;
DROP TABLE IF EXISTS playlists CASCADE;
DROP TABLE IF EXISTS playlist_subscriptions CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS follows CASCADE;
DROP TABLE IF EXISTS conversation_participants CASCADE;
DROP TABLE IF EXISTS direct_messages CASCADE;
DROP TABLE IF EXISTS conversations CASCADE;
DROP TABLE IF EXISTS contents CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- 1. 사용자 테이블
CREATE TABLE users (
                       id VARCHAR(36) PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       locked BOOLEAN DEFAULT FALSE,
                       profile_image_url VARCHAR(255),
                       auth_provider VARCHAR(50),
                       provider_user_id VARCHAR(255),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 콘텐츠 테이블
CREATE TABLE contents (
                          id VARCHAR(36) PRIMARY KEY,
                          type VARCHAR(50) NOT NULL,
                          title VARCHAR(255) NOT NULL,
                          description TEXT,
                          thumbnail_url VARCHAR(255),
                          average_rating DOUBLE DEFAULT 0.0,
                          review_count INT DEFAULT 0,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. 리뷰 테이블
CREATE TABLE reviews (
                         id VARCHAR(36) PRIMARY KEY,
                         user_id VARCHAR(36) NOT NULL,
                         content_id VARCHAR(36) NOT NULL,
                         content TEXT NOT NULL,
                         rating DOUBLE NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(id),
                         FOREIGN KEY (content_id) REFERENCES contents(id)
);

-- 4. 팔로우 테이블
CREATE TABLE follows (
                         follower_id VARCHAR(36) NOT NULL,
                         following_id VARCHAR(36) NOT NULL,
                         PRIMARY KEY (follower_id, following_id),
                         FOREIGN KEY (follower_id) REFERENCES users(id),
                         FOREIGN KEY (following_id) REFERENCES users(id)
);

-- 5. 대화방 테이블
CREATE TABLE conversations (
                               id VARCHAR(36) PRIMARY KEY,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. 대화 참여자 테이블
CREATE TABLE conversation_participants (
                                           conversation_id VARCHAR(36) NOT NULL,
                                           user_id VARCHAR(36) NOT NULL,
                                           joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                           last_read_at TIMESTAMP,
                                           PRIMARY KEY (conversation_id, user_id),
                                           FOREIGN KEY (conversation_id) REFERENCES conversations(id),
                                           FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 7. 다이렉트 메시지 테이블
CREATE TABLE direct_messages (
                                 id VARCHAR(36) PRIMARY KEY,
                                 conversation_id VARCHAR(36) NOT NULL,
                                 sender_id VARCHAR(36) NOT NULL,
                                 receiver_id VARCHAR(36) NOT NULL,
                                 content TEXT NOT NULL,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 FOREIGN KEY (conversation_id) REFERENCES conversations(id),
                                 FOREIGN KEY (sender_id) REFERENCES users(id),
                                 FOREIGN KEY (receiver_id) REFERENCES users(id)
);

-- 8. 알림 테이블
CREATE TABLE notifications (
                               id VARCHAR(36) PRIMARY KEY,
                               receiver_id VARCHAR(36) NOT NULL,
                               title VARCHAR(255) NOT NULL,
                               content VARCHAR(255) NOT NULL,
                               level VARCHAR(50) NOT NULL, -- INFO, WARNING, ERROR 등
                               read_at TIMESTAMP,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (receiver_id) REFERENCES users(id)
);

-- 9. 플레이리스트 테이블
CREATE TABLE playlists (
                           id VARCHAR(36) PRIMARY KEY,
                           user_id VARCHAR(36) NOT NULL,
                           title VARCHAR(255) NOT NULL,
                           description TEXT,
                           subscriber_count INT DEFAULT 0,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 10. 플레이리스트 콘텐츠 테이블 (중간 테이블)
CREATE TABLE playlist_contents (
                                   id VARCHAR(36) PRIMARY KEY,
                                   playlist_id VARCHAR(36) NOT NULL,
                                   content_id VARCHAR(36) NOT NULL,
                                   FOREIGN KEY (playlist_id) REFERENCES playlists(id),
                                   FOREIGN KEY (content_id) REFERENCES contents(id)
);

-- 11. 태그 테이블
CREATE TABLE tags (
                      id VARCHAR(36) PRIMARY KEY,
                      tag VARCHAR(255) NOT NULL UNIQUE
);

-- 12. 콘텐츠-태그 중간 테이블
CREATE TABLE content_tags (
                              id VARCHAR(36) PRIMARY KEY,
                              content_id VARCHAR(36) NOT NULL,
                              tag_id VARCHAR(36) NOT NULL,
                              FOREIGN KEY (content_id) REFERENCES contents(id),
                              FOREIGN KEY (tag_id) REFERENCES tags(id)
);