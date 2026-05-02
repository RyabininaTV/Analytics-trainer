import {
  createBrowserRouter,
  createRoutesFromElements,
  Link,
  Route,
} from "react-router-dom";
import MainLayout from "../layouts/mainLayout/mainLayout";
import { AuthPage } from "../pages/authPage";

export const router = createBrowserRouter(
  createRoutesFromElements(
    <>
      <Route path="/" element={<MainLayout />}>
        <Route
          index
          element={
            <div>
              <h1>trainers</h1>
              <ul>
                <li>
                  <Link to={`trainers/${1}`}>1</Link>
                </li>
                <li>
                  <Link to={`trainers/${2}`}>2</Link>
                </li>
                <li>
                  <Link to={`trainers/${3}`}>3</Link>
                </li>
              </ul>
            </div>
          }
        />
        <Route path="trainers/:slug" element={<h1>trainer №...</h1>} />
      </Route>
      <Route path="login" element={<AuthPage />} />
      <Route path="registration" element={<AuthPage />} />
    </>,
  ),
);
