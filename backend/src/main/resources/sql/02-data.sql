USE volunteer_management;

INSERT INTO role (id, role_code, role_name, description) VALUES
(1, 'ADMIN', '管理员', '系统管理员'),
(2, 'VOLUNTEER', '志愿者', '普通志愿者')
ON DUPLICATE KEY UPDATE
role_code = VALUES(role_code),
role_name = VALUES(role_name),
description = VALUES(description);

INSERT INTO user (id, username, password_hash, real_name, phone, status) VALUES
(1, 'admin', '$2b$10$m3KFhVB1n5tF.aGnowfbfuBLzs2l0mp0AV6dLhf8ReTOKM4HZiYBy', '系统管理员', '13800000000', 1),
(2, 'volunteer01', '$2b$10$m3KFhVB1n5tF.aGnowfbfuBLzs2l0mp0AV6dLhf8ReTOKM4HZiYBy', '张三', '13900000000', 1)
ON DUPLICATE KEY UPDATE
username = VALUES(username),
password_hash = VALUES(password_hash),
real_name = VALUES(real_name),
phone = VALUES(phone),
status = VALUES(status);

INSERT INTO user_role (id, user_id, role_id) VALUES
(1, 1, 1),
(2, 2, 2)
ON DUPLICATE KEY UPDATE
user_id = VALUES(user_id),
role_id = VALUES(role_id);

INSERT INTO volunteer_profile (id, user_id, student_or_member_no, gender, age, community_name, skill_tag, remark) VALUES
(1, 2, 'V2026001', '男', 20, '第一社区', '宣传,秩序维护', '示例志愿者档案')
ON DUPLICATE KEY UPDATE
user_id = VALUES(user_id),
student_or_member_no = VALUES(student_or_member_no),
gender = VALUES(gender),
age = VALUES(age),
community_name = VALUES(community_name),
skill_tag = VALUES(skill_tag),
remark = VALUES(remark);
