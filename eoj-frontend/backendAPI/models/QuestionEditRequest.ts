/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { JudgeCase } from "./JudgeCase";
import type { JudgeConfig } from "./JudgeConfig";

export type QuestionEditRequest = {
  answer?: string;
  content?: string;
  difficulty?: number;
  id?: number;
  judgeCase?: Array<JudgeCase>;
  judgeConfig?: JudgeConfig;
  tags?: Array<string>;
  title?: string;
};

