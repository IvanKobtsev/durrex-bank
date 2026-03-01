declare module "*.svg?react" {
  const content: React.FC<
    React.SVGProps<SVGSVGElement> & {
      testId?: string;
      title?: string;
      className?: string;
    }
  >;
  export default content;
}
declare module "*.ttf" {}

declare module "*.png";
declare module "*.jpg";
declare module "*.jpeg";
declare module "*.gif";
declare module "*.webp";

declare module "*.wav";
declare module "*.mp3";
