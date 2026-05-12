import { AuthForm } from "../../modules/authForm";
import styles from "./authPage.module.scss";

const AuthPage = () => {
  return (
    <div className={styles.authPage}>
      <AuthForm />
    </div>
  );
};

export default AuthPage;
