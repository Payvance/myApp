import React from 'react';
import './TransactionHistory.css';

const TransactionHistory = ({ data }) => {
  if (!data || !data.length) return null;

  const formatAmount = (amount) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0
    }).format(amount);
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', {
      day: '2-digit',
      month: 'short'
    });
  };

  return (
    <div className="transaction-history-card">
      <h3 className="transaction-history__title">Transaction History</h3>
      
      <div className="transaction-history__table-wrapper">
        <table className="transaction-history__table">
          <thead>
            <tr>
              <th className="transaction-history__header">Date</th>
              <th className="transaction-history__header">Type</th>
              <th className="transaction-history__header">Amount</th>
              <th className="transaction-history__header">Status</th>
            </tr>
          </thead>
          <tbody>
            {data.slice(0, 5).map((transaction, index) => (
              <tr key={index} className="transaction-history__row">
                <td className="transaction-history__cell">
                  {formatDate(transaction.date)}
                </td>
                <td className="transaction-history__cell">
                  <span className={`transaction-history__type transaction-history__type--${transaction.type.toLowerCase()}`}>
                    {transaction.type}
                  </span>
                </td>
                <td className="transaction-history__cell">
                  <span className={`transaction-history__amount transaction-history__amount--${transaction.type.toLowerCase()}`}>
                    {transaction.type === 'Credit' ? '+' : '-'}{formatAmount(transaction.amount)}
                  </span>
                </td>
                <td className="transaction-history__cell">
                  <span className={`transaction-history__status transaction-history__status--${transaction.status.toLowerCase()}`}>
                    {transaction.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default TransactionHistory;
