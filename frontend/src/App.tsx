import React, { useEffect, useState } from 'react';
import './App.scss';
import { ConfigProvider, Empty, Layout, message } from 'antd';
import { Content, Footer, Header } from 'antd/lib/layout/layout';
import Navigation from './components/navigation/Navigation';
import ProtectedRoute from './components/routes/ProtectedRoute';
import { User, UserRole } from './types/User';
import AdminOverview from './components/adminOverview/AdminOverview';
import DirectorOverview from './components/directorOverview/DirectorOverview';
import Login from './components/login/Login';
import Unauthorized from './components/routes/Unauthorized';
import Overview from './components/overview/Overview';
import { Route, BrowserRouter as Router, Routes, Outlet } from 'react-router-dom';
import { AppRoutes } from './types/AppRoutes';
import { CopyrightOutlined } from '@ant-design/icons';
import { AuthContext } from './context/UserContext';
import Settings from './components/settings/Settings';
import Register from './components/register/Register';
import VerifyAccount from './components/verify/VerifyAccount';

const App: React.FC = () => {

  const [loggedUser, setLoggedUser] = useState<User | undefined>(undefined);
  const login = (user: User) => setLoggedUser(user);
  const logout = () => {
    setLoggedUser(undefined);
    message.info("Sie wurden ausgeloggt", 2);
  }
  const hasRoles = (roles: Array<UserRole>): boolean => {
    if (loggedUser) {
      // has to include all requested roles
      return roles
        .map(role => loggedUser.roles.includes(role))
        .reduce((prev, curr) => prev && curr);
    } else {
      return false;
    }
  }

  const MainLayout = () => {
    return (
      <Layout>
        <Header>
          <Navigation />
        </Header>

        <Content style={{ padding: '50px' }}>
          <ConfigProvider renderEmpty={() =>
            <Empty
              description="Keine Daten verfügbar">
            </Empty>
          }>

            <div className="site-layout-content">
              <Outlet />
            </div>

          </ConfigProvider>
        </Content>

        <Footer>
          <CopyrightOutlined /> 2021
        </Footer>

      </Layout>
    );
  }

  return (
    <div className="App">

      <AuthContext.Provider
        value={{
          loggedUser: loggedUser,
          login: login,
          logout: logout,
          hasRoles: hasRoles
        }}>

        <Router >
          <Routes>
            <Route path={AppRoutes.Main.Path} element={<MainLayout />}>
              <Route path={AppRoutes.Main.Path} element={<Overview />} />
              <Route path={AppRoutes.Main.Subroutes.Login} element={<Login />} />
              <Route path={AppRoutes.Main.Subroutes.Register} element={<Register />} />
              <Route
                path={AppRoutes.Main.Subroutes.AdminOverview}
                element={
                  <ProtectedRoute hasAccess={hasRoles([UserRole.ROLE_ADMIN])}>
                    <AdminOverview />
                  </ProtectedRoute>} />
              <Route
                path={AppRoutes.Main.Subroutes.DirectorOverview}
                element={
                  <ProtectedRoute hasAccess={hasRoles([UserRole.ROLE_DIRECTOR])}>
                    <DirectorOverview />
                  </ProtectedRoute>} />
              <Route
                path={AppRoutes.Main.Subroutes.Settings}
                element={
                  <ProtectedRoute hasAccess={loggedUser ? true : false}>
                    <Settings />
                  </ProtectedRoute>} />
            </Route>

            <Route path={AppRoutes.Verify} element={<VerifyAccount />} />
            <Route path={AppRoutes.Unauthorized} element={<Unauthorized />} />

            <Route path="*" element={<Unauthorized />} />
          </Routes>
        </Router>

      </AuthContext.Provider>
    </div>
  );
}

export default App;
