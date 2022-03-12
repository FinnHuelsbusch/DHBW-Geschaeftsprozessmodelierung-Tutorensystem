import React, { useEffect, useState } from 'react';
import './App.scss';
import { ConfigProvider, Empty, Layout, message, Result } from 'antd';
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
import { CopyrightOutlined, LoadingOutlined } from '@ant-design/icons';
import { AuthContext, UserContext } from './context/UserContext';
import Settings from './components/settings/Settings';
import Register from './components/register/Register';
import VerifyRegistration from './components/verify/VerifyRegistration';
import { applyUserLogin, isUserLoginExpired, removeUserLogin, retrieveUserLocalStorage } from './api/api';
import VerifyResetPassword from './components/verify/VerifyResetPassword';
import { validateMessages } from './utils/Messages';
import locale from 'antd/lib/locale/de_DE';
import 'moment/locale/de'
import TutorialsOverview from './components/tutorials/TutorialsOverview';
import TutorialDetails from './components/tutorials/TutorialDetails';


const App: React.FC = () => {

  const [initialPageLoadComplete, setInitialPageLoadComplete] = useState(false);
  const [loggedUser, setLoggedUser] = useState<User | undefined>(undefined);

  const UserContext: UserContext = {
    login: (user: User, remember: boolean = false) => {
      applyUserLogin(user, remember);
      setLoggedUser(user);
    },
    logout: () => {
      removeUserLogin();
      setLoggedUser(undefined);
    },
    hasRoles: (roles: Array<UserRole>): boolean => {
      if (loggedUser) {
        // has to include all requested roles
        return roles
          .map(role => loggedUser.roles.includes(role))
          .reduce((prev, curr) => prev && curr);
      } else {
        return false;
      }
    },
    loggedUser: loggedUser
  };


  useEffect(() => {
    // initial opening of page: log in user if persisted
    const user = retrieveUserLocalStorage();
    if (user) {
      if (!user.jwt || isUserLoginExpired(user.loginExpirationDate)) {
        // login has expired
        UserContext.logout();
        message.warn("Login abgelaufen");
      } else {
        UserContext.login(user);
      }
    }
    setInitialPageLoadComplete(true);
  }, []);


  const MainLayout = () => {
    return (
      <Layout>
        <Header>
          <Navigation />
        </Header>

        <Content style={{ padding: '50px' }}>
          <ConfigProvider locale={locale} form={{ validateMessages }} renderEmpty={() =>
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

  const MainContent = () => {
    return (
      <Router>
        <Routes>
          <Route path={AppRoutes.Main.Path} element={<MainLayout />}>
            <Route path={AppRoutes.Main.Path} element={<Overview />} />
            <Route path={AppRoutes.Main.Subroutes.Login} element={<Login />} />
            <Route path={AppRoutes.Main.Subroutes.Register} element={<Register />} />
            <Route path={"tutorials/:tutorialId"}
              element={<TutorialDetails />} />
            <Route path={AppRoutes.Main.Subroutes.Tutorials} element={<TutorialsOverview />} />
            <Route
              path={AppRoutes.Main.Subroutes.AdminOverview}
              element={
                <ProtectedRoute hasAccess={UserContext.hasRoles([UserRole.ROLE_ADMIN])}>
                  <AdminOverview />
                </ProtectedRoute>} />
            <Route
              path={AppRoutes.Main.Subroutes.DirectorOverview}
              element={
                <ProtectedRoute hasAccess={UserContext.hasRoles([UserRole.ROLE_DIRECTOR])}>
                  <DirectorOverview />
                </ProtectedRoute>} />
            <Route
              path={AppRoutes.Main.Subroutes.Settings}
              element={
                <ProtectedRoute hasAccess={loggedUser ? true : false}>
                  <Settings />
                </ProtectedRoute>} />
          </Route>

          <Route path={AppRoutes.VerifyRegistration} element={<VerifyRegistration />} />
          <Route path={AppRoutes.VerifyResetPassword} element={<VerifyResetPassword />} />

          <Route path={AppRoutes.Unauthorized} element={<Unauthorized />} />

          <Route path="*" element={<Unauthorized />} />
        </Routes>
      </Router>
    );
  }

  return (
    <div className="App">
      <AuthContext.Provider value={{ ...UserContext }}>
        {initialPageLoadComplete ? <MainContent />
          : <Result
            icon={<LoadingOutlined />}>
          </Result>
        }
      </AuthContext.Provider>
    </div>
  );
}

export default App;
