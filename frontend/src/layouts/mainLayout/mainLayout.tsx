import { Suspense, useEffect, type FC } from "react";
import {
  Link,
  NavLink,
  Outlet,
  useNavigate,
  type NavLinkRenderProps,
} from "react-router-dom";
import styles from "./mainLayout.module.scss";
import { Button, Col } from "antd";
import { useLogoutQuery } from "../../hooks/api";
import { getFromLocalStorage } from "../../utils";
import type { RegistrationResponse } from "../../hooks/api/useRegistrationQuery";
import { LoadingSpin } from "../../components/loadingSpin";
import { BackSpaceView } from "../../components/backSpaceView";

const menuItemHandler = ({ isActive }: NavLinkRenderProps) => {
  return isActive ? styles.active : "";
};

const MainLayout: FC = () => {
  const navigate = useNavigate();

  const logout = useLogoutQuery();

  const user = getFromLocalStorage<RegistrationResponse>("user");
  const isAuth = user ? true : false;

  useEffect(() => {
    if (!isAuth) navigate("login");
  }, [isAuth, navigate]);

  return (
    <div>
      <header className={styles.header}>
        <nav>
          <ul>
            <li>
              <NavLink to={"/"} className={menuItemHandler}>
                <span>Тренажеры</span>
              </NavLink>
            </li>
          </ul>
        </nav>
        {!isAuth ? (
          <Link
            to={"login"}
            onClick={(e) => {
              e.stopPropagation();
            }}
          >
            <Button color="primary" variant="outlined">
              Войти
            </Button>
          </Link>
        ) : (
          <Button
            onClick={() => logout.mutate()}
            color="danger"
            variant="outlined"
          >
            Выйти
          </Button>
        )}
      </header>
      <main>
        <Col span={4}>
          <BackSpaceView />
        </Col>
        <Col span={16}>
          <Suspense fallback={<LoadingSpin />}>
            <Outlet />
          </Suspense>
        </Col>
        <Col span={4} />
      </main>
    </div>
  );
};

export default MainLayout;
