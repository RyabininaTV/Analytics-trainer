import { useGetUserProfile } from "../../hooks/api/useGetUserProfile";
import mockIcon from "../../assets/react.svg";
import styles from "./profilePage.module.scss";
import { userStatus } from "../../constants/constants";

const ProfilePage = () => {
  const { data: profile } = useGetUserProfile();

  return (
    <article className={styles.profilePage}>
      <div className={styles.mockImage}>
        <div className={styles.imgWrapper}>
          <div>
            <img src={mockIcon} alt="" />
            <p>Здесь будет ваше фото</p>
          </div>
        </div>
      </div>
      <div className={styles.userInfo}>
        <p className={styles.userName}>{profile?.username}</p>
        {profile?.status && (
          <div
            className={[
              styles.status,
              profile?.status === "ACTIVE" ? styles.ACTIVE : styles.BLOCKED,
            ].join(" ")}
          >
            {userStatus[profile?.status]}
          </div>
        )}
        <p className={styles.text}>
          <span>Роль: </span>
          <span>{profile?.role}</span>
        </p>
        <p className={styles.text}>
          <span>Почта: </span>
          <span>{profile?.email}</span>
        </p>
      </div>
    </article>
  );
};

export default ProfilePage;
