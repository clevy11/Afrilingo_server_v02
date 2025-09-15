-- Ensure profile_picture can store long base64 strings
ALTER TABLE user_profiles
    ALTER COLUMN profile_picture TYPE text;
