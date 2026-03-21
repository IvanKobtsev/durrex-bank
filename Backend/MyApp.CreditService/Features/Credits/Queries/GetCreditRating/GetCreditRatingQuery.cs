using MediatR;

public record GetCreditRatingQuery(int ClientId) : IRequest<CreditRatingResponse>;

public record CreditRatingResponse(int ClientId, int Score, DateTime CalculatedAt);
