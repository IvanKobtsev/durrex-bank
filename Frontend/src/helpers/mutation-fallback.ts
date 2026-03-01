export async function mutateAsync<CRT, RT>(props: {
  callback: () => Promise<CRT>;
  onSuccess: (result: CRT) => Promise<RT>;
  onError: (error: unknown) => Promise<RT> | RT;
}) {
  let result: CRT;

  try {
    result = await props.callback();
  } catch (e) {
    await props.onError?.(e);
    throw e;
  }

  await props.onSuccess(result);
}
