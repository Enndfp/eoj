import type { JudgeInfo } from "./JudgeInfo";

export type QuestionRunResponse = {
  input?: string;
  output?: string;
  judgeInfo?: JudgeInfo;
};
