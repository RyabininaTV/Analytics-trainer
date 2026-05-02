import { Button, Card, Form, Input } from "antd";
import { type FC } from "react";
import styles from "./authForm.module.scss";
import { NavLink, useLocation } from "react-router-dom";
import { useLoginQuery, useRegistrationQuery } from "../../hooks/api";
import type { LoginFieldsType, RegFieldsType } from "../../api/authApi";

const { useForm } = Form;

const currentAuthType = (title: string, isActive: boolean) => (
  <Button type={isActive ? "primary" : "default"}>{title}</Button>
);

const AuthForm: FC = () => {
  const location = useLocation();
  const [form] = useForm();

  const registration = useRegistrationQuery(form);
  const login = useLoginQuery();

  return (
    <Card
      className={styles.authCard}
      extra={
        <>
          <NavLink
            to={"/login"}
            className={styles.authType}
            children={({ isActive }) => currentAuthType("Вход", isActive)}
          />
          <NavLink
            to={"/registration"}
            className={styles.authType}
            children={({ isActive }) =>
              currentAuthType("Регистрация", isActive)
            }
          />
        </>
      }
    >
      <Form
        form={form}
        name="atLoginForm"
        layout="vertical"
        requiredMark="optional"
        onFinish={(fields: RegFieldsType | LoginFieldsType) => {
          if ("username" in fields) {
            registration.mutate(fields);
          } else {
            login.mutate(fields);
          }
        }}
      >
        <Form.Item<RegFieldsType>
          label="Почта"
          name={"email"}
          rules={[{ required: true, message: "Укажите Ваш email!" }]}
        >
          <Input />
        </Form.Item>
        {location.pathname === "/registration" && (
          <Form.Item<RegFieldsType>
            label="Имя"
            name={"username"}
            rules={[{ required: true, message: "Укажите Ваше имя!" }]}
          >
            <Input />
          </Form.Item>
        )}
        <Form.Item<RegFieldsType>
          label="Пароль"
          name={"password"}
          rules={[{ required: true, message: "Укажите Ваш пароль!" }]}
        >
          <Input.Password />
        </Form.Item>

        {registration.isError && registration.error.status === 409 && (
          <div className={styles.authError}>Пользователь уже существует</div>
        )}

        <Form.Item>
          <Button
            htmlType="submit"
            type="primary"
            className={styles.submitButton}
          >
            {location.pathname === "/login" ? "Войти" : "Зарегистрироваться"}
          </Button>
        </Form.Item>
      </Form>
    </Card>
  );
};

export default AuthForm;
