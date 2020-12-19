int ComputeFactorial(int n)
{
	int sum = 1;
	int i;
	for(i = 1;i <= n;i = i + 1;)
	{
		sum = sum * i;
	}
	return sum;
}

int main()
{
	int n = inputNum();
	int none;
	int res = ComputeFactorial(n);
	none = printNum(res);

	if(res >1000)
		none = printNum(1);
	else 
		none = printNum(0);
}