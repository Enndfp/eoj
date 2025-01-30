import AccessEnum from "@/access/accessEnum";

/**
 * 检查权限（判断当前登录用户是否具有某个权限）
 * @param loginUser 当前登录用户
 * @param needAccess 需要有的权限
 * @return boolean 有无权限
 */
const checkAccess = (loginUser: any, needAccess = AccessEnum.NOT_LOGIN) => {
  // 获取当前登录用户具有的权限（如果没有loginUser，则表示未登录）
  const loginUserAccess = loginUser?.userRole ?? AccessEnum.NOT_LOGIN;

  // 访问的页面不需要权限就返回true
  if (needAccess === AccessEnum.NOT_LOGIN) {
    return true;
  }

  // 如果用户登录才能访问
  if (needAccess === AccessEnum.USER) {
    // 用户没登录就返回false
    if (loginUserAccess === AccessEnum.NOT_LOGIN) {
      return false;
    }
  }

  // 如果需要管理员权限
  if (needAccess === AccessEnum.ADMIN) {
    // 用户不是管理员就返回false
    if (loginUserAccess !== AccessEnum.ADMIN) {
      return false;
    }
  }

  return true;
};

export default checkAccess;
