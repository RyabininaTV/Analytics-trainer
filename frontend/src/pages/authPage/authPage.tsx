import { AuthForm } from "../../modules/authForm";
import styles from "./authPage.module.scss";

const AuthPage = () => {
  return (
    <article className={styles.authPage}>
      <AuthForm />
    </article>
  );
};

export default AuthPage;
