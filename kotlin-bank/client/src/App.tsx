import React from "react";

const googleClientId = "<TODO>"; // Google 클라이언트 ID// GitHub OAuth2 설정
const githubClientId = "<TODO>"; // GitHub 클라이언트 ID

const serverCallbackUrl = "http://localhost:9090/api/v1/auth/callback"; // 서버 콜백 URL

const App: React.FC = () => {

  const loginWithGoogle = () => {
    const scope = "email profile";
    const authUrl = `https://accounts.google.com/o/oauth2/auth?client_id=${googleClientId}&redirect_uri=${encodeURIComponent(serverCallbackUrl)}&response_type=code&scope=${scope}&state=google`;

    window.location.href = authUrl; 
  };

  const loginWithGitHub = () => {
    const scope = "user:email";
    const authUrl = `https://github.com/login/oauth/authorize?client_id=${githubClientId}&redirect_uri=${encodeURIComponent(serverCallbackUrl)}&scope=${scope}&state=github`;

    window.location.href = authUrl;
  };

  return (
    <div>
      <h2>OAuth2 로그인</h2>
      <button onClick={loginWithGoogle}>Google 로그인</button>
      <button onClick={loginWithGitHub}>GitHub 로그인</button>
    </div>
  );
};

export default App;