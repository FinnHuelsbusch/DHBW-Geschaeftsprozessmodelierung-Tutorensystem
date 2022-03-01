import React, { useState } from 'react';
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
import { Route, BrowserRouter as Router, Routes } from 'react-router-dom';
import { AppRoutes } from './types/AppRoutes';
import { CopyrightOutlined } from '@ant-design/icons';
import { AuthContext } from './context/UserContext';
import Settings from './components/settings/Settings';
import Register from './components/register/Register';

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

  return (
    <div className="App">
      <Router >
        <AuthContext.Provider
          value={{
            loggedUser: loggedUser,
            login: login,
            logout: logout,
            hasRoles: hasRoles
          }}>

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
                  <Routes>

                    <Route path={AppRoutes.Login} element={<Login />} />
                    <Route path={AppRoutes.Register} element={<Register />} />
                    <Route path={AppRoutes.Unauthorized} element={<Unauthorized />} />
                    <Route path={AppRoutes.Home} element={<Overview />} />
                    <Route
                      path={AppRoutes.AdminOverview}
                      element={
                        <ProtectedRoute hasAccess={hasRoles([UserRole.ROLE_ADMIN])}>
                          <AdminOverview />
                        </ProtectedRoute>} />
                    <Route
                      path={AppRoutes.DirectorOverview}
                      element={
                        <ProtectedRoute hasAccess={hasRoles([UserRole.ROLE_DIRECTOR])}>
                          <DirectorOverview />
                        </ProtectedRoute>} />
                    <Route
                      path={AppRoutes.Settings}
                      element={
                        <ProtectedRoute hasAccess={loggedUser ? true : false}>
                          <Settings />
                        </ProtectedRoute>} />
                    <Route path="/*" element={<Unauthorized />} />
                  </Routes>
                </div>

              </ConfigProvider>
            </Content>

            <Footer>
              <CopyrightOutlined /> 2021
            </Footer>

          </Layout>
        </AuthContext.Provider>

      </Router>
    </div>
  );
}

export default App;
