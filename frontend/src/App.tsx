import React, { useState } from 'react';
import './App.css';
import { ConfigProvider, Empty, Layout } from 'antd';
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

const App: React.FC = () => {

  const [loggedUser, setLoggedUser] = useState<User | undefined>(undefined);

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
                  <Route path={AppRoutes.Unauthorized} element={<Unauthorized />} />
                  <Route path={AppRoutes.Home} element={<Overview />} />
                  <Route
                    path={AppRoutes.AdminOverview}
                    element={
                      <ProtectedRoute hasAccess={hasRoles([UserRole.ROLE_DIRECTOR])}>
                        <AdminOverview />
                      </ProtectedRoute>} />
                  <Route
                    path={AppRoutes.DirectorOverview}
                    element={
                      <ProtectedRoute hasAccess={hasRoles([UserRole.ROLE_DIRECTOR])}>
                        <DirectorOverview />
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
      </Router>
    </div>
  );
}

export default App;
